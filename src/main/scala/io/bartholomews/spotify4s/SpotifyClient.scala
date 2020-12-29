package io.bartholomews.spotify4s

import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.config.UserAgent
import io.bartholomews.fsclient.core.oauth.ClientPasswordAuthentication
import io.bartholomews.fsclient.core.oauth.v2.ClientPassword
import io.bartholomews.spotify4s.api._
import pureconfig.ConfigSource
import sttp.client.SttpBackend

class SpotifyClient[F[_]](client: FsClient[F, ClientPasswordAuthentication]) {
  type S = ClientPasswordAuthentication
  object auth extends AuthApi[F](client)
  object browse extends BrowseApi[F, S](client)
  object follow extends FollowApi[F, S](client)
  object playlists extends PlaylistsApi[F, S](client)
  object tracks extends TracksApi[F, S](client)
  object users extends UsersApi[F, S](client)
}

object SpotifyClient {
  import pureconfig.generic.auto._

  def unsafeFromConfig[F[_]]()(implicit sttpBackend: SttpBackend[F, Nothing, Nothing]): SpotifyClient[F] = {
    // FIXME: Should add User-Agent header to all requests, also make it safe
    val userAgent = ConfigSource.default.at(namespace = "user-agent").loadOrThrow[UserAgent]
    val signer = ClientPasswordAuthentication(
      ConfigSource.default.at("spotify").loadOrThrow[ClientPassword]
    )
    new SpotifyClient[F](FsClient(userAgent, signer, sttpBackend))
  }
}
