package io.bartholomews.spotify4s

import cats.effect.{ContextShift, IO}
import io.bartholomews.fsclient.client.FsClientV2
import io.bartholomews.fsclient.config.{FsClientConfig, UserAgent}
import io.bartholomews.fsclient.entities.oauth.v2.OAuthV2AuthorizationFramework.ClientPassword
import io.bartholomews.fsclient.entities.oauth.{ClientPasswordBasicAuthenticationV2, SignerV2}
import io.bartholomews.spotify4s.api._
import pureconfig.ConfigSource

import scala.concurrent.ExecutionContext

class SpotifyClient(client: FsClientV2[IO, SignerV2])(
  implicit ec: ExecutionContext
) {
  private implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)

  object auth extends AuthApi(client)
  object browse extends BrowseApi(client)
  object playlists extends PlaylistsApi(client)
  object tracks extends TracksApi(client)
  object users extends UsersApi(client)
  object follow extends FollowApi(client)
}

object SpotifyClient {
  import FsClientConfig.LoadConfigOrThrow
  import pureconfig.generic.auto._

  def unsafeFromConfig()(implicit ec: ExecutionContext, cs: ContextShift[IO]): SpotifyClient =
    (for {
      userAgent <- ConfigSource.default.at(namespace = "user-agent").load[UserAgent]
      clientPassword <- ConfigSource.default
                         .at("spotify")
                         .load[ClientPassword]
    } yield new SpotifyClient(
      new FsClientV2(
        appConfig = FsClientConfig(userAgent, ClientPasswordBasicAuthenticationV2(clientPassword)),
        clientPassword
      )
    )).orThrow
}
