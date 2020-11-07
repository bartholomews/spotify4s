package io.bartholomews.spotify4s

import cats.effect.{ConcurrentEffect, ContextShift}
import io.bartholomews.fsclient.client.FsClientV2
import io.bartholomews.fsclient.config.{FsClientConfig, UserAgent}
import io.bartholomews.fsclient.entities.oauth.v2.OAuthV2AuthorizationFramework.ClientPassword
import io.bartholomews.fsclient.entities.oauth.{ClientPasswordBasicAuthenticationV2, SignerV2}
import io.bartholomews.spotify4s.api._
import pureconfig.ConfigSource

import scala.concurrent.ExecutionContext

class SpotifyClient[F[_]: ConcurrentEffect](client: FsClientV2[F, SignerV2])(
  implicit ec: ExecutionContext
) {
  object auth extends AuthApi[F](client)
  object browse extends BrowseApi[F, SignerV2](client)
  object playlists extends PlaylistsApi[F, SignerV2](client)
  object tracks extends TracksApi[F, SignerV2](client)
  object users extends UsersApi[F, SignerV2](client)
  object follow extends FollowApi[F, SignerV2](client)
}

object SpotifyClient {
  import FsClientConfig.LoadConfigOrThrow
  import pureconfig.generic.auto._

  def unsafeFromConfig[F[_]: ConcurrentEffect]()(implicit ec: ExecutionContext, cs: ContextShift[F]): SpotifyClient[F] =
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
