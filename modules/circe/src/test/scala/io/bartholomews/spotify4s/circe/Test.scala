package io.bartholomews.spotify4s.circe

import cats.data.NonEmptySet
import io.bartholomews.fsclient.core.config.UserAgent
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.{AccessToken, RefreshToken}
import io.bartholomews.fsclient.core.oauth.v2.{ClientId, ClientPassword, ClientSecret}
import io.bartholomews.fsclient.core.oauth.{AccessTokenSigner, RedirectUri, Scope}
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.circe.Test._
import io.bartholomews.spotify4s.circe.codecs._
import io.bartholomews.spotify4s.core.api.AlbumsApi.AlbumIds
import io.bartholomews.spotify4s.core.api.AuthApi.SpotifyUserAuthorizationRequest
import io.bartholomews.spotify4s.core.api.TracksApi.TrackIds
import io.bartholomews.spotify4s.core.entities.SpotifyId.{SpotifyAlbumId, SpotifyTrackId}
import io.bartholomews.spotify4s.core.entities.SpotifyScope
import io.bartholomews.spotify4s.core.{SpotifyAuthClient, SpotifySimpleClient}
import sttp.client3.{HttpURLConnectionBackend, Identity, Response, ResponseException, UriContext}

object Test {
  // $COVERAGE-OFF$
  val userAgent: UserAgent =
    UserAgent("spotify4s", Some("0.0.1"), Some("https://github.com/bartholomews/spotify4s"))

  val clientPassword: ClientPassword = ClientPassword(
    clientId = ClientId(System.getenv("MUSICGENE_SPOTIFY_CLIENT_ID")),
    clientSecret = ClientSecret(System.getenv("MUSICGENE_SPOTIFY_CLIENT_SECRET"))
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

  val sttpClient: SpotifyAuthClient[Identity] = {
    new SpotifyAuthClient(
      userAgent,
      clientPassword,
      HttpURLConnectionBackend()
    )
  }
  // $COVERAGE-ON$
}

object SpotifySimpleClientFlow extends App {
  import Test._
  val simpleClient = new SpotifySimpleClient(userAgent, clientPassword, HttpURLConnectionBackend())
  println {
    simpleClient.albums
      .getAlbums(
        AlbumIds.fromList(List(SpotifyAlbumId("1weenld61qoidwYuZ1GESA"))).getOrElse(throw new Exception("OOPS")),
        None
      )
      .body
  }
}

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
      nonRefreshableToken => {
        // 2. Use access token
        sttpClient.albums
          .getNewReleases(
            country = Some(CountryCodeAlpha2.ITALY),
            limit = 3,
            offset = 2
          )(nonRefreshableToken)
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
    Some(RefreshToken(refreshToken)),
    Scope(List(""))
  )

  printBody {
    // FIXME: Deserialization error
    sttpClient.tracks.getTracks(
      ids = TrackIds
        .fromNes(
          NonEmptySet.of(
            SpotifyTrackId("458LTQbp2xTIIBtguCOFbU"),
            SpotifyTrackId("2Eg21mDTQ3tk1OiPSnONwq")
//        SpotifyTrackId("") // FIXME: I think SpotifyId needs to be nonEmpty otherwise troubles (400)
          )
        )
        .getOrElse(throw new Exception("2 > 50 ? W.T.F.")),
      market = None
    )(authorizationCodeResponse)
  }
  // $COVERAGE-ON$
}

object AuthorizationCodeFlow_4_RefreshAccessToken extends App {
  val refreshToken: RefreshToken = RefreshToken(AuthorizationCodeFlow_3_UseAccessToken.refreshToken)
  printBody {
    sttpClient.auth.AuthorizationCode.refresh(refreshToken)
  }
}
