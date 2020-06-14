package io.bartholomews.spotify4s

import cats.effect.{ContextShift, IO}
import io.bartholomews.fsclient.client.FsClientV2
import io.bartholomews.fsclient.config.{FsClientConfig, UserAgent}
import io.bartholomews.fsclient.entities.oauth.v2.OAuthV2AuthorizationFramework._
import io.bartholomews.fsclient.entities.oauth.{AuthorizationCode, ClientPasswordBasicAuthenticationV2, NonRefreshableToken, Scope}
import io.bartholomews.fsclient.entities.{ErrorBodyJson, ErrorBodyString, FsResponse}
import io.bartholomews.fsclient.utils.HttpTypes.HttpResponse
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.Test.{client, prettyPrint, _}
import io.bartholomews.spotify4s.entities.{SpotifyError, SpotifyId, SpotifyScope}
import org.http4s.Uri

import scala.concurrent.ExecutionContext

object Test {
  // $COVERAGE-OFF$
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)

  def prettyPrint[A](res: IO[HttpResponse[A]]): Unit = println {
    res.unsafeRunSync() match {
      case FsResponse(headers, status, Right(body)) =>
        println(headers)
        println(status)
        println(body)

      case FsResponse(_, status, Left(ErrorBodyString(error))) =>
        println("~~~~~~~~~~~~~~~~~~ TEXT RESPONSE ~~~~~~~~~~~~~~~~~~~~~~~~~")
        println(status)
        println(error)

      case FsResponse(_, status, Left(ErrorBodyJson(error))) =>
        println("~~~~~~~~~~~~~~~~~~ JSON RESPONSE ~~~~~~~~~~~~~~~~~~~~~~~~~")
        println(status)
        println(error.as[SpotifyError])
    }
  }

  private val clientPassword =
    ClientPassword(
      clientId = ClientId(System.getenv("SPOTIFY4S_CLIENT_ID")),
      clientSecret = ClientSecret(System.getenv("SPOTIFY4S_CLIENT_SECRET"))
    )

  private val userAgent =
    UserAgent("spotify4s", Some("0.0.1"), Some("https://github.com/bartholomews/spotify4s"))

  val client: SpotifyClient = new SpotifyClient(
    new FsClientV2(
      appConfig = FsClientConfig(userAgent, ClientPasswordBasicAuthenticationV2(clientPassword)),
      clientPassword
    )
  )
  // $COVERAGE-ON$
}

object GetClientCredentialsToken extends App {
  // $COVERAGE-OFF$
  prettyPrint {
    client.auth.clientCredentials()
  }
  // $COVERAGE-ON$
}

object UseClientCredentialsToken extends App {
  // $COVERAGE-OFF$
  private val accessTokenValue = "???"
  implicit val clientCredentialsToken: NonRefreshableToken = NonRefreshableToken(
    generatedAt = 10000000001L,
    accessToken = AccessToken(accessTokenValue),
    tokenType = "Bearer",
    expiresIn = 3600,
    scope = Scope(List(""))
  )

  import eu.timepit.refined.auto.autoRefineV

  prettyPrint {
    Test.client.browse.getNewReleases(
      country = Some(CountryCodeAlpha2.CAMBODIA),
      limit = 3,
      offset = 2
    )
  }
  // $COVERAGE-ON$
}

object GetAuthorizeUrl extends App {
  // $COVERAGE-OFF$
  println {
    client.auth.authorizeUrl(
      Uri.unsafeFromString("https://bartholomews.io"),
      state = Some("sticazzi"),
      scopes = List(
        SpotifyScope.PLAYLIST_READ_PRIVATE,
        SpotifyScope.APP_REMOTE_CONTROL,
        SpotifyScope.PLAYLIST_MODIFY_PUBLIC
      )
    )
  }
  // $COVERAGE-ON$
}

object UseAuthorizeUrl extends App {
  // $COVERAGE-OFF$
  prettyPrint {
    client.auth.AuthorizationCode.fromUri(
      Uri.unsafeFromString(
        "https://bartholomews.io/?code=AQAt3JCrA9xd6r_h134LkRsNCR8QvmxPlFpaS3R8JkeFsBG9GQGvnNw6NpyQOMtgjacV6Vf1XkRrTYkMDV7WPJlXS8jyNOfBZJLgEwSmiBr4owQEIAVwLHNFQwA2hwLRva8rEteDSToPCJUKBPMrRjiY77WDzPCz95p0vcj3Cd5cP-g3HFJmzul8j4mJdL8OETfb4oC6CpyRw9WfVGTTiL2J1fVkfBZfMjdzrY6IHPsckqNPHgV04td0Mt1sCSnTos6M1nSgIlftle-rFrYGqw&state=sticazzi"
      )
    )
  }
  // $COVERAGE-ON$
}

object UseAccessToken {
  // $COVERAGE-OFF$
  val accessToken =
    "BQBgbxKBSxASSyTP1T3AOuKJEpm6ydwC2lO1ER18FxFr3x4jdYmZXgWa7YJpVdr6yREJucuERMkSCsHH4ZwJQuiY84p7uj3RIVgKqsk_EKYdCiwGJJ9kHgb78n5f7UmvHJRqWS44rB7ymsdLRYtEVbynYlFgFkuWLP3PtShjl_YXpFtV1zf1O8o"

  val refreshToken =
    "AQB5n_17lSA2ROEMte8YFLjZXGX2IzHO2QF9w5xIQbxX-yoqd7za-x2GzI--c504jI1GYxrMCuqUqhA_hElfq3MtM2tShQu4X5ZQivU6poFmJWZCHgr7Ob0kJFWX36ScrOY"

  implicit val authorizationCodeResponse: AuthorizationCode = AuthorizationCode(
    generatedAt = 10000000001L,
    AccessToken(accessToken),
    "Bearer",
    3600,
    Some(RefreshToken(refreshToken)),
    Scope(List(""))
  )

  def main(args: Array[String]): Unit = {
    prettyPrint {

      Test.client.tracks.getTracks(
        ids = Set(
          SpotifyId("458LTQbp2xTIIBtguCOFbU"),
          SpotifyId("2Eg21mDTQ3tk1OiPSnONwq"),
          SpotifyId(""), // FIXME: I think SpotifyId needs to be nonEmpty otherwise troubles (400)
        ),
        market = None
      )

//      Example.client.browse.getNewReleases(
//        country = Some(CountryCodeAlpha2.ITALY),
//        limit = 3,
//        offset = 2
//      )
    }
  }
  // $COVERAGE-ON$
}

object RefreshAccessToken {
  def main(args: Array[String]): Unit = {
    val refreshToken = RefreshToken(UseAccessToken.refreshToken)
    prettyPrint {
      client.auth.AuthorizationCode.refresh(refreshToken)
    }
  }
}
