package io.bartholomews.spotify4s.core

import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.config.UserAgent
import io.bartholomews.fsclient.core.oauth.ClientPasswordAuthentication
import io.bartholomews.fsclient.core.oauth.v2.ClientPassword
import io.bartholomews.spotify4s.core.api._
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures
import sttp.client3.SttpBackend

class SpotifyAuthClient[F[_]] private (client: FsClient[F, ClientPasswordAuthentication]) {
  def this(userAgent: UserAgent, clientPassword: ClientPassword, backend: SttpBackend[F, Any]) =
    this(FsClient(userAgent, ClientPasswordAuthentication(clientPassword), backend))

  type S = ClientPasswordAuthentication
  object auth extends AuthApi[F](client)
  object albums extends AlbumsApi[F, S](client)
  object categories extends CategoriesApi[F, S](client)
  object genres extends GenresApi[F, S](client)
  object playlists extends PlaylistsApi[F, S](client)
  object tracks extends TracksApi[F, S](client)
  object users extends UsersApi[F, S](client)
}

object SpotifyAuthClient {
  import pureconfig.generic.auto._

  private val userAgentConfig = ConfigSource.default.at(namespace = "user-agent")
  private val spotifyConfig = ConfigSource.default.at("spotify")

  def fromConfig[F[_]](sttpBackend: SttpBackend[F, Any]): Either[ConfigReaderFailures, SpotifyAuthClient[F]] =
    for {
      userAgent <- userAgentConfig.load[UserAgent]
      clientPassword <- spotifyConfig.load[ClientPassword]
    } yield new SpotifyAuthClient[F](userAgent, clientPassword, sttpBackend)

  def unsafeFromConfig[F[_]](sttpBackend: SttpBackend[F, Any]): SpotifyAuthClient[F] = {
    val userAgent = userAgentConfig.loadOrThrow[UserAgent]
    val clientPassword = spotifyConfig.loadOrThrow[ClientPassword]
    new SpotifyAuthClient[F](userAgent, clientPassword, sttpBackend)
  }
}
