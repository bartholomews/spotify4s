import it.turingtest.spotify.scala.client.AuthApi
import it.turingtest.spotify.scala.client.entities.{AuthError, Token, USER_READ_BIRTHDATE, USER_READ_EMAIL}
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.Configuration
import play.api.test.WsTestClient

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
class AuthApiSpec extends FunSpec with Matchers with GuiceOneServerPerTest with SpotifyWebMock {

  describe("Auth Api") {

    it("should have a constructor with default uri to api endpoints") {
      WsTestClient.withClient { client =>
        val authApi = new AuthApi(config, client)
        authApi.AUTHORIZE_ENDPOINT shouldBe "https://accounts.spotify.com/authorize"
        authApi.TOKEN_ENDPOINT shouldBe "https://accounts.spotify.com/api/token"
      }
    }

    val WRONG_CLIENT_ID = Configuration.apply(
      ("CLIENT_ID", "some-wrong-id"),
      ("CLIENT_SECRET", CLIENT_SECRET),
      ("REDIRECT_URI", REDIRECT_URI)
    )

    val WRONG_CLIENT_SECRET = Configuration.apply(
      ("CLIENT_ID", CLIENT_ID),
      ("CLIENT_SECRET", "some-wrong-secret"),
      ("REDIRECT_URI", REDIRECT_URI)
    )

    describe("Authorise url") {

      it("should create an url with the right default parameters") {
        withAuthApi { auth =>
          val result = auth.authoriseURL
          assert(result.contains(s"client_id=$CLIENT_ID"))
          assert(result.contains(s"redirect_uri=$REDIRECT_URI"))
          assert(result.contains("show_dialog=true"))
        }
      }

      it("should create an url with the right explicit parameters") {
        withAuthApi { auth =>
          val result = auth.authoriseURL(
            state = Some("kind-of-state"),
            scopes = List(USER_READ_EMAIL, USER_READ_BIRTHDATE),
            showDialog = false
          )
          assert(result.contains(s"client_id=$CLIENT_ID"))
          assert(result.contains(s"redirect_uri=$REDIRECT_URI"))
          assert(result.contains("state=kind-of-state"))
          assert(result.contains("scope=user-read-email+user-read-birthdate"))
          assert(result.contains("show_dialog=false"))
        }
      }
    }

    describe("Client credentials flow") {

      it("should receive an access token on a client credentials request") {
        withAuthApi { auth =>
          val result = await {
            auth.clientCredentials
          }
          result.status shouldBe 200
          val token = result.json.validate[Token].get
          token.access_token shouldBe "some-access-token"
        }
      }

      it("should receive a Bad Request error if the client is invalid") {
        withAuthApi { auth =>
          val result = await { auth.clientCredentials }
          result.status shouldBe 400
          val response = result.json.validate[AuthError].get
          response.error shouldBe "invalid_client"
          response.message shouldBe "Invalid client"
        }(WRONG_CLIENT_ID)
      }

      it("should receive a Bad Request error if the secret is invalid") {
        withAuthApi { auth =>
          val result = await { auth.clientCredentials }
          result.status shouldBe 400
          val response = result.json.validate[AuthError].get
          response.error shouldBe "invalid_client"
          response.message shouldBe "Invalid client secret"
        }(WRONG_CLIENT_SECRET)
      }
    }

    describe("Authorization code grant flow") {

      it("should get an AuthError for a request with invalid Authorization code") {
        withAuthApi { auth =>
          val result = await {
            auth.accessToken("invalid-code")
          }
          result.status shouldBe 400
          val response = result.json.validate[AuthError].get
          response.error shouldBe "invalid_grant"
          response.message shouldBe "Invalid authorization code"
        }
      }

      it("should get a valid token from a request with valid Authorization code") {
        withAuthApi { auth =>
          val result = await {
            auth.accessToken("valid_code")
          }
          result.status shouldBe 200
          val token = result.json.validate[Token].get
          token.access_token shouldBe "some-access-token"
        }
      }

      it("should get an AuthError for a request with invalid refresh token") {
        withAuthApi { auth =>
          val result = await {
            auth.refreshToken("wrong-refresh")
          }
          result.status shouldBe 400
          val response = result.json.validate[AuthError].get
          response.error shouldBe "invalid_grant"
          response.message shouldBe "Invalid refresh token"
        }
      }

      it("should be able to exchange an expired token with a fresh one") {
        withAuthApi { auth =>
          val result = await {
            auth.refreshToken("refresh-token")
          }
          result.status shouldBe 200
          val token = result.json.validate[Token].get
          token.access_token shouldBe "kind-of-refresh-token"
        }
      }
    }

  }

}
