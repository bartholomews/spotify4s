package io.bartholomews.spotify4s.api

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.entities.oauth.v2.OAuthV2AuthorizationFramework._
import io.bartholomews.fsclient.entities.oauth.{AuthorizationCode, NonRefreshableToken, Scope}
import io.bartholomews.fsclient.entities.{ErrorBodyString, FsResponse}
import io.bartholomews.fsclient.utils.HttpTypes.IOResponse
import io.bartholomews.spotify4s.entities.SpotifyScope
import io.bartholomews.testudo.WireWordSpec
import org.http4s.{Status, Uri}

// http://blog.shangjiaming.com/2018/01/04/http4s-intorduction/
// https://www.lewuathe.com/wiremock-in-scala.html
class AuthApiSpec extends WireWordSpec with ServerBehaviours {
  import io.bartholomews.spotify4s.client.ClientData._

  "AuthorizationCode" when {
    val sampleCode = "sample-oauth-code"

    "getting an authorization code" when {
      def expectedTokenEndpointRequest: MappingBuilder = post(urlMatching("/accounts/api/token"))

      def clientReceivingSuccessfulAuthorizationCode(
        request: IOResponse[AuthorizationCode]
      ): Unit = {
        "the server responds with the expected json message" should {
          def stub: StubMapping =
            stubFor(
              expectedTokenEndpointRequest
                .withRequestBody(
                  equalTo(
                    "grant_type=authorization_code&code=sample-oauth-code&redirect_uri=https%3A%2F%2Fbartholomews.io%2Fcallback"
                  )
                )
                .willReturn(
                  aResponse()
                    .withStatus(200)
                    .withBodyFile("auth/authorization_access.json")
                )
            )

          "return a Right with the response Token" in matchResponse(stub, request) {
            case FsResponse(_, _, Right(authorizationCode)) =>
              authorizationCode should matchTo(
                AuthorizationCode(
                  accessToken = AccessToken("some-access-token"),
                  tokenType = "Bearer",
                  expiresIn = 3600L,
                  refreshToken = RefreshToken("refresh-token"),
                  scope = Scope(List("user-read-private", "user-read-email", "unknown-scope"))
                )
              )
              SpotifyScope.fromScope(authorizationCode.scope) shouldBe List(
                SpotifyScope.USER_READ_PRIVATE,
                SpotifyScope.USER_READ_EMAIL
              )
          }
        }
      }

      "getting an authorization code from code and redirect uri" when {
        val request: IOResponse[AuthorizationCode] = sampleClient.auth.AuthorizationCode(
          code = sampleCode,
          redirectUri = RedirectUri(Uri.unsafeFromString("https://bartholomews.io/callback"))
        )

        behave like clientReceivingSuccessfulAuthorizationCode(request)
        behave like clientReceivingUnexpectedResponse(expectedTokenEndpointRequest, request)
      }

      "getting an authorization code from invalid uri response" in {
        val request: IOResponse[AuthorizationCode] = sampleClient.auth.AuthorizationCode.fromUri(
          Uri.unsafeFromString("https://bartholomews.io/callback?state=some_state")
        )

        inside(request.unsafeRunSync()) {
          case FsResponse(_, status, Left(ErrorBodyString(message))) =>
            status shouldBe Status.Unauthorized
            message should matchTo("missing_required_query_parameters")
        }
      }

      "getting an authorization code from rejected uri response" in {
        val request: IOResponse[AuthorizationCode] = sampleClient.auth.AuthorizationCode.fromUri(
          Uri.unsafeFromString("https://bartholomews.io/callback?error=access_denied&state=some_state")
        )

        inside(request.unsafeRunSync()) {
          case FsResponse(_, status, Left(ErrorBodyString(message))) =>
            status shouldBe Status.Unauthorized
            message should matchTo("access_denied")
        }
      }

      "getting an authorization code from valid uri response" when {
        def request: IOResponse[AuthorizationCode] = sampleClient.auth.AuthorizationCode.fromUri(
          Uri.unsafeFromString(s"https://bartholomews.io/callback?code=$sampleCode&state=some_state")
        )

        behave like clientReceivingSuccessfulAuthorizationCode(request)
        behave like clientReceivingUnexpectedResponse(expectedTokenEndpointRequest, request)
      }
    }

    "getting a refresh token" when {
      def expectedTokenEndpointRequest: MappingBuilder = post(urlMatching("/accounts/api/token"))
      val request: IOResponse[AuthorizationCode] = sampleClient.auth.AuthorizationCode.refresh(
        OAuthV2.sampleRefreshToken
      )

      "the server responds with the expected json message" should {
        def stub: StubMapping =
          stubFor(
            expectedTokenEndpointRequest
              .withRequestBody(equalTo(s"grant_type=refresh_token&refresh_token=${OAuthV2.sampleRefreshToken.value}"))
              .willReturn(
                aResponse()
                  .withStatus(200)
                  .withBodyFile("auth/authorization_access.json")
              )
          )

        "return a Right with the response Token" in matchResponse(stub, request) {
          case FsResponse(_, _, Right(authorizationCode)) =>
            authorizationCode should matchTo(
              AuthorizationCode(
                accessToken = AccessToken("some-access-token"),
                tokenType = "Bearer",
                expiresIn = 3600L,
                refreshToken = RefreshToken("refresh-token"),
                scope = Scope(List("user-read-private", "user-read-email", "unknown-scope"))
              )
            )
            SpotifyScope.fromScope(authorizationCode.scope) shouldBe List(
              SpotifyScope.USER_READ_PRIVATE,
              SpotifyScope.USER_READ_EMAIL
            )
        }
      }
    }

    "clientCredentials" when {
      val request: IOResponse[NonRefreshableToken] = sampleClient.auth.clientCredentials()
      def expectedTokenEndpointRequest: MappingBuilder = post(urlMatching("/accounts/api/token"))
      behave like clientReceivingUnexpectedResponse(expectedTokenEndpointRequest, request)

      "the server responds with the expected json message" should {
        def stub: StubMapping =
          stubFor(
            expectedTokenEndpointRequest
              .withRequestBody(equalTo("grant_type=client_credentials"))
              .willReturn(
                aResponse()
                  .withStatus(200)
                  .withBodyFile("auth/client_credentials.json")
              )
          )

        "return a Right with the response Token" in matchResponse(stub, request) {
          case FsResponse(_, _, Right(token)) =>
            token should matchTo(
              NonRefreshableToken(
                accessToken = AccessToken("some-access-token"),
                tokenType = "bearer",
                expiresIn = 3600L,
                scope = Scope(List.empty)
              )
            )
        }
      }
    }

    "authorizeUrl" should {
      val sampleRedirectUri = Uri.unsafeFromString("https://bartholomews.io/callback")

      "return the correct Uri with default parameters" in {
        val request: Uri = sampleClient.auth.authorizeUrl(
          redirectUri = sampleRedirectUri,
          state = None,
          scopes = List.empty
        )

        request should matchTo(
          Uri
            .unsafeFromString("http://127.0.0.1:8080/accounts/authorize")
            .withQueryParam("show_dialog", false)
            .withQueryParam("client_id", "SAMPLE_CLIENT_ID")
            .withQueryParam("response_type", "code")
            .withQueryParam("redirect_uri", sampleRedirectUri.renderString)
        )
      }

      "return the correct Uri with explicit parameters" in {
        val request: Uri = sampleClient.auth.authorizeUrl(
          redirectUri = sampleRedirectUri,
          state = Some("1010101010101010101"),
          scopes = List(SpotifyScope.PLAYLIST_MODIFY_PRIVATE, SpotifyScope.PLAYLIST_READ_PRIVATE),
          showDialog = true
        )

        request should matchTo(
          Uri
            .unsafeFromString("http://127.0.0.1:8080/accounts/authorize")
            .withQueryParam("show_dialog", true)
            .withQueryParam("client_id", "SAMPLE_CLIENT_ID")
            .withQueryParam("response_type", "code")
            .withQueryParam("redirect_uri", sampleRedirectUri.renderString)
            .withQueryParam("state", "1010101010101010101")
            .withQueryParam("scope", List("playlist-modify-private playlist-read-private"))
        )
      }
    }
  }
}
