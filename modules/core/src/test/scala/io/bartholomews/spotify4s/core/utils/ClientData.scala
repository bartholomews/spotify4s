package io.bartholomews.spotify4s.core.utils

import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.config.UserAgent
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.{AccessToken, RefreshToken}
import io.bartholomews.fsclient.core.oauth.v2.{ClientId, ClientPassword, ClientSecret}
import io.bartholomews.fsclient.core.oauth.{
  AccessTokenSigner,
  ClientPasswordAuthentication,
  NonRefreshableTokenSigner,
  Scope
}
import io.bartholomews.spotify4s.core.SpotifyClient
import io.bartholomews.spotify4s.core.entities.{SpotifyId, SpotifyUserId}
import sttp.client3.{HttpURLConnectionBackend, Identity}

object ClientData {
  val sampleSpotifyId: SpotifyId = SpotifyId("SAMPLE_SPOTIFY_ID")
  val sampleSpotifyUserId: SpotifyUserId = SpotifyUserId("SAMPLE_SPOTIFY_USER_ID")

  val sampleUserAgent: UserAgent = UserAgent(
    appName = "SAMPLE_APP_NAME",
    appVersion = Some("SAMPLE_APP_VERSION"),
    appUrl = Some("https://bartholomews.io/sample-app-url")
  )

  val sampleClientId: ClientId = ClientId("SAMPLE_CLIENT_ID")
  val sampleClientSecret: ClientSecret = ClientSecret("SAMPLE_CLIENT_SECRET")
  val sampleClientPassword: ClientPassword = ClientPassword(sampleClientId, sampleClientSecret)

  def sampleFsClient: FsClient[Identity, ClientPasswordAuthentication] = FsClient(
    sampleUserAgent,
    ClientPasswordAuthentication(sampleClientPassword),
    HttpURLConnectionBackend()
  )

  val sampleAccessTokenKey: AccessToken = AccessToken(
    "00000000000-0000000000000000000-0000000-0000000000000000000000000000000000000000001"
  )

  val sampleRefreshToken: RefreshToken = RefreshToken("SAMPLE_REFRESH_TOKEN")

  val sampleAuthorizationCode: AccessTokenSigner = AccessTokenSigner(
    generatedAt = 21312L,
    accessToken = sampleAccessTokenKey,
    tokenType = "bearer",
    expiresIn = 1000L,
    refreshToken = Some(sampleRefreshToken),
    scope = Scope(List.empty)
  )

  val sampleNonRefreshableToken: NonRefreshableTokenSigner = NonRefreshableTokenSigner(
    generatedAt = 21312L,
    accessToken = sampleAccessTokenKey,
    tokenType = "bearer",
    expiresIn = 1000L,
    scope = Scope(List.empty)
  )

  val sampleClient: SpotifyClient[Identity] = new SpotifyClient(sampleFsClient)
}
