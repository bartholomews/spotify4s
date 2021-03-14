package io.bartholomews.spotify4s.circe

import cats.data.NonEmptySet
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.config.UserAgent
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.{AccessToken, RedirectUri, RefreshToken}
import io.bartholomews.fsclient.core.oauth.v2.{ClientId, ClientPassword, ClientSecret}
import io.bartholomews.fsclient.core.oauth.{AccessTokenSigner, ClientPasswordAuthentication, Scope}
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.circe.Test._
import io.bartholomews.spotify4s.core.SpotifyClient
import io.bartholomews.spotify4s.core.api.AuthApi.SpotifyUserAuthorizationRequest
import io.bartholomews.spotify4s.core.entities.{SpotifyId, SpotifyScope}
import sttp.client3.{HttpURLConnectionBackend, Identity, Response, ResponseException, UriContext}
import io.bartholomews.spotify4s.circe.codecs._

object Test {
  // $COVERAGE-OFF$
  private val userAgent =
    UserAgent("spotify4s", Some("0.0.1"), Some("https://github.com/bartholomews/spotify4s"))

  private val signer = ClientPasswordAuthentication(
    ClientPassword(
      clientId = ClientId(System.getenv("MUSICGENE_SPOTIFY_CLIENT_ID")),
      clientSecret = ClientSecret(System.getenv("MUSICGENE_SPOTIFY_CLIENT_SECRET"))
    )
  )

  val spotifyUserAuthorizationRequest: SpotifyUserAuthorizationRequest =
    SpotifyUserAuthorizationRequest(
      state = Some("wat"),
      redirectUri = RedirectUri(uri"https://bartholomews.io/callback"),
      scopes = List(
        SpotifyScope.PLAYLIST_READ_PRIVATE,
        SpotifyScope.APP_REMOTE_CONTROL,
        SpotifyScope.PLAYLIST_MODIFY_PUBLIC
      )
    )

  def printBody[DE, A](re: Response[Either[ResponseException[String, DE], A]]): Unit =
    re.body.fold(println, println)

  val sttpClient: SpotifyClient[Identity] = {
    new SpotifyClient[Identity](
      FsClient(userAgent, signer, HttpURLConnectionBackend())
    )
  }
  // $COVERAGE-ON$
}
/*
#authorization-code-flow"
#authorization-code-flow-with-proof-key-for-code-exchange-pkce
#implicit-grant-flow
#client-credentials-flow
 */

// https://developer.spotify.com/documentation/general/guides/authorization-guide/#client-credentials-flow
object ClientCredentialsFlow extends App {
  // $COVERAGE-OFF$
  import eu.timepit.refined.auto.autoRefineV
  // 1. Request access token
  sttpClient.auth
    .clientCredentials[io.circe.Error]
    .body
    .fold(
      println,
      implicit nonRefreshableToken => {
        // 2. Use access token
        sttpClient.browse
          .getNewReleases(
            country = Some(CountryCodeAlpha2.ITALY),
            limit = 3,
            offset = 2
          )
          .body
          .fold(println, println)
      }
    )
  // $COVERAGE-ON$
}

// https://developer.spotify.com/documentation/general/guides/authorization-guide/#authorization-code-flow
object AuthorizationCodeFlow_1_GetAuthorizeUrl extends App {
  // $COVERAGE-OFF$

  println {
    sttpClient.auth.authorizeUrl(Test.spotifyUserAuthorizationRequest)
  }
  // $COVERAGE-ON$
}

object AuthorizationCodeFlow_2_UseAuthorizeUrl extends App {
  // $COVERAGE-OFF$
  println {
    val redirectionUriResponse =
      uri"???"

    println(redirectionUriResponse.toString())
    sttpClient.auth.AuthorizationCode.acquire[io.circe.Error](
      Test.spotifyUserAuthorizationRequest,
      redirectionUriResponse
    )
  }
  // $COVERAGE-ON$
}

object AuthorizationCodeFlow_3_UseAccessToken extends App {
  // $COVERAGE-OFF$
  def accessToken =
    "???"

  def refreshToken =
    "???"

  implicit val authorizationCodeResponse: AccessTokenSigner = AccessTokenSigner(
    generatedAt = 10000000001L,
    AccessToken(accessToken),
    "Bearer",
    3600,
    RefreshToken(refreshToken),
    Scope(List(""))
  )

  printBody {
    // FIXME: Deserialization error
    sttpClient.tracks.getTracks(
      ids = NonEmptySet.of(
        SpotifyId("458LTQbp2xTIIBtguCOFbU"),
        SpotifyId("2Eg21mDTQ3tk1OiPSnONwq")
//        SpotifyId("") // FIXME: I think SpotifyId needs to be nonEmpty otherwise troubles (400)
      ),
      market = None
    )
  }
  // $COVERAGE-ON$
}

object AuthorizationCodeFlow_4_RefreshAccessToken extends App {
  val refreshToken: RefreshToken = RefreshToken(AuthorizationCodeFlow_3_UseAccessToken.refreshToken)
  printBody {
    sttpClient.auth.AuthorizationCode.refresh(refreshToken)
  }
}
