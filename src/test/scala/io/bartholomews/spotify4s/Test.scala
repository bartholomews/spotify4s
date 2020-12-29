package io.bartholomews.spotify4s

import cats.data.NonEmptySet
import cats.effect.{ContextShift, IO}
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.config.UserAgent
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.{AccessToken, RedirectUri, RefreshToken}
import io.bartholomews.fsclient.core.oauth.v2.{ClientId, ClientPassword, ClientSecret}
import io.bartholomews.fsclient.core.oauth.{AccessTokenSigner, ClientPasswordAuthentication, Scope}
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.Test._
import io.bartholomews.spotify4s.entities.{SpotifyId, SpotifyScope}
import io.circe.Error
import sttp.client.{HttpURLConnectionBackend, Identity, Response, ResponseError, UriContext}

import scala.concurrent.ExecutionContext

object Test {
  // $COVERAGE-OFF$
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)

  private val userAgent =
    UserAgent("spotify4s", Some("0.0.1"), Some("https://github.com/bartholomews/spotify4s"))

  private val signer = ClientPasswordAuthentication(
    ClientPassword(
      clientId = ClientId(System.getenv("MUSICGENE_SPOTIFY_CLIENT_ID")),
      clientSecret = ClientSecret(System.getenv("MUSICGENE_SPOTIFY_CLIENT_SECRET"))
    )
  )

  def printBody[A](re: Response[Either[ResponseError[Error], A]]): Unit =
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
    .clientCredentials()
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

  def authorizationCodeRequest = sttpClient.auth.authorizationCodeRequest(
    redirectUri = RedirectUri(uri"https://bartholomews.io/callback"),
    state = Some("wat"),
    scopes = List(
      SpotifyScope.PLAYLIST_READ_PRIVATE,
      SpotifyScope.APP_REMOTE_CONTROL,
      SpotifyScope.PLAYLIST_MODIFY_PUBLIC
    )
  )

  println {
    sttpClient.auth.authorizeUrl(authorizationCodeRequest)
  }
  // $COVERAGE-ON$
}

object AuthorizationCodeFlow_2_UseAuthorizeUrl extends App {
  // $COVERAGE-OFF$
  println {
    val uri =
      uri"???"

    println(uri.toString())
    sttpClient.auth.AuthorizationCode.getAccessToken(
      AuthorizationCodeFlow_1_GetAuthorizeUrl.authorizationCodeRequest,
      uri
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
    Some(RefreshToken(refreshToken)),
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
    sttpClient.auth.AuthorizationCode.getRefreshToken(refreshToken)
  }
}
