package io.bartholomews.spotify4s.core

import cats.Monad
import io.bartholomews.fsclient.core.config.UserAgent
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.v2.ClientPassword
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.ResponseHandler
import io.bartholomews.fsclient.core.oauth.{ClientPasswordAuthentication, NonRefreshableTokenSigner, TokenSignerV2}
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.api.AlbumsApi.AlbumIds
import io.bartholomews.spotify4s.core.api.SpotifyApi.Offset
import io.bartholomews.spotify4s.core.entities._
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures
import sttp.client3.SttpBackend

/**
  * This client has a subset of available endpoints available,
  * as it is not suitable for user-related endpoints (it would return 401);
  * it manages (and refresh) a single Client Credentials token.
  */
class SpotifySimpleClient[F[_]: Monad] private (client: SpotifyAuthClient[F]) {
  def this(userAgent: UserAgent, clientPassword: ClientPassword, backend: SttpBackend[F, Any]) =
    this(new SpotifyAuthClient(userAgent, clientPassword, backend))

  import eu.timepit.refined.auto.autoRefineV
  type S = ClientPasswordAuthentication

  private var signer: Option[TokenSignerV2] = None

  import cats.implicits._

  private def acquireToken[E, A](f: TokenSignerV2 => F[SttpResponse[E, A]])(
    implicit tokenHandler: ResponseHandler[E, NonRefreshableTokenSigner]
  ): F[SttpResponse[E, A]] = {
    client.auth.clientCredentials.flatMap(
      (response: SttpResponse[E, NonRefreshableTokenSigner]) =>
        response.body.fold(
          err => response.copy(body = err.asLeft[A]).pure[F],
          newToken => {
            signer = Some(newToken)
            f(newToken)
          }
      )
    )
  }

  private def withToken[E, A](f: TokenSignerV2 => F[SttpResponse[E, A]])(
    implicit tokenHandler: ResponseHandler[E, NonRefreshableTokenSigner]
  ): F[SttpResponse[E, A]] =
    signer.fold(acquireToken(f))(
      token =>
        if (token.isExpired()) acquireToken(f)
        else f(token)
    )

  object albums {
    def getAlbum[E](id: SpotifyId, country: Option[CountryCodeAlpha2])(
      implicit
      tokenHandler: ResponseHandler[E, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[E, FullAlbum]
    ): F[SttpResponse[E, FullAlbum]] = withToken { client.albums.getAlbum(id, country) }

    def getAlbums[E](ids: AlbumIds, country: Option[CountryCodeAlpha2])(
      implicit
      tokenHandler: ResponseHandler[E, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[E, FullAlbumsResponse]
    ): F[SttpResponse[E, List[FullAlbum]]] = withToken { client.albums.getAlbums(ids, country) }

    def getAlbumTracks[E](
      id: SpotifyId,
      country: Option[CountryCodeAlpha2],
      limit: FullTrack.Limit = 20,
      offset: Offset = 0
    )(
      implicit
      tokenHandler: ResponseHandler[E, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[E, Page[SimpleTrack]]
    ): F[SttpResponse[E, Page[SimpleTrack]]] =
      withToken { client.albums.getAlbumTracks(id, country, limit, offset) }
  }
}

object SpotifySimpleClient {
  import pureconfig.generic.auto._

  private val userAgentConfig = ConfigSource.default.at(namespace = "user-agent")
  private val spotifyConfig = ConfigSource.default.at("spotify")

  def fromConfig[F[_]: Monad](sttpBackend: SttpBackend[F, Any]): Either[ConfigReaderFailures, SpotifySimpleClient[F]] =
    for {
      userAgent <- userAgentConfig.load[UserAgent]
      clientPassword <- spotifyConfig.load[ClientPassword]
    } yield new SpotifySimpleClient[F](userAgent, clientPassword, sttpBackend)

  def unsafeFromConfig[F[_]: Monad](sttpBackend: SttpBackend[F, Any]): SpotifySimpleClient[F] = {
    val userAgent = userAgentConfig.loadOrThrow[UserAgent]
    val clientPassword = spotifyConfig.loadOrThrow[ClientPassword]
    new SpotifySimpleClient[F](userAgent, clientPassword, sttpBackend)
  }
}
