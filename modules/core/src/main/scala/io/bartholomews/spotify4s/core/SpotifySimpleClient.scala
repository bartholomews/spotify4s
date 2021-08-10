package io.bartholomews.spotify4s.core

import cats.Monad
import io.bartholomews.fsclient.core.config.UserAgent
import io.bartholomews.fsclient.core.http.SttpResponses.{ResponseHandler, SttpResponse}
import io.bartholomews.fsclient.core.oauth.v2.ClientPassword
import io.bartholomews.fsclient.core.oauth.{ClientPasswordAuthentication, NonRefreshableTokenSigner, TokenSignerV2}
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.api.AlbumsApi
import io.bartholomews.spotify4s.core.api.AlbumsApi.AlbumIds
import io.bartholomews.spotify4s.core.api.FollowApi.UserIdsFollowingPlaylist
import io.bartholomews.spotify4s.core.api.SpotifyApi.Offset
import io.bartholomews.spotify4s.core.entities.SpotifyId.{SpotifyAlbumId, SpotifyPlaylistId, SpotifyUserId}
import io.bartholomews.spotify4s.core.entities._
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures
import sttp.client3.SttpBackend

import java.util.concurrent.atomic.AtomicReference

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

  private val signerRef: AtomicReference[Option[TokenSignerV2]] = new AtomicReference(None)

  import cats.implicits._

  private def acquireToken[E, A](f: TokenSignerV2 => F[SttpResponse[E, A]])(
    implicit tokenHandler: ResponseHandler[E, NonRefreshableTokenSigner]
  ): F[SttpResponse[E, A]] = {
    client.auth.clientCredentials.flatMap(
      (response: SttpResponse[E, NonRefreshableTokenSigner]) =>
        response.body.fold(
          err => response.copy(body = err.asLeft[A]).pure[F],
          newToken => {
            signerRef.set(Some(newToken))
            f(newToken)
          }
        )
    )
  }

  private def withToken[E, A](f: TokenSignerV2 => F[SttpResponse[E, A]])(
    implicit tokenHandler: ResponseHandler[E, NonRefreshableTokenSigner]
  ): F[SttpResponse[E, A]] =
    signerRef
      .get()
      .fold(acquireToken(f))(
        token =>
          if (token.isExpired()) acquireToken(f)
          else f(token)
      )

  object albums {
    def getAlbums[E](ids: AlbumIds, market: Option[CountryCodeAlpha2])(
      implicit
      tokenHandler: ResponseHandler[E, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[E, FullAlbumsResponse]
    ): F[SttpResponse[E, List[FullAlbum]]] = withToken { client.albums.getAlbums(ids, market) }

    def getAlbum[E](id: SpotifyAlbumId, country: Option[CountryCodeAlpha2])(
      implicit
      tokenHandler: ResponseHandler[E, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[E, FullAlbum]
    ): F[SttpResponse[E, FullAlbum]] = withToken { client.albums.getAlbum(id, country) }

    def getAlbumTracks[E](
      id: SpotifyAlbumId,
      market: Option[CountryCodeAlpha2],
      limit: AlbumsApi.TracksLimit = 20,
      offset: Offset = 0
    )(
      implicit
      tokenHandler: ResponseHandler[E, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[E, Page[SimpleTrack]]
    ): F[SttpResponse[E, Page[SimpleTrack]]] =
      withToken { client.albums.getAlbumTracks(id, market, limit, offset) }
  }

  object follow {
    def usersFollowingPlaylist[E](playlistId: SpotifyPlaylistId, userIds: UserIdsFollowingPlaylist)(
      implicit
      tokenHandler: ResponseHandler[E, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[E, List[Boolean]]
    ): F[SttpResponse[E, Map[SpotifyUserId, Boolean]]] =
      withToken { client.follow.usersFollowingPlaylist(playlistId, userIds) }
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
