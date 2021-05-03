package io.bartholomews.spotify4s.core.api

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.core.http.FsClientSttpExtensions.UriExtensions
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.{AccessToken, RefreshToken}
import io.bartholomews.fsclient.core.oauth.{AccessTokenSigner, NonRefreshableTokenSigner, RedirectUri, Scope}
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.scalatestudo.data.ClientData.v2.sampleRefreshToken
import io.bartholomews.scalatestudo.diff.DiffDerivations
import io.bartholomews.spotify4s.core.SpotifyServerBehaviours
import io.bartholomews.spotify4s.core.api.AuthApi.SpotifyUserAuthorizationRequest
import io.bartholomews.spotify4s.core.entities.SpotifyScope
import io.bartholomews.spotify4s.core.utils.SpotifyClientData.sampleClient
import sttp.client3.{HttpError, Identity, UriContext}
import sttp.model.StatusCode

// http://blog.shangjiaming.com/2018/01/04/http4s-intorduction/
// https://www.lewuathe.com/wiremock-in-scala.html
abstract class AuthApiSpec[E[_], D[_], DE, J]
    extends WireWordSpec
    with SpotifyServerBehaviours[E, D, DE, J]
    with DiffDerivations {
  implicit def accessTokenSignerDecoder: D[AccessTokenSigner]
  implicit def nonRefreshableTokenSignerDecoder: D[NonRefreshableTokenSigner]

  "AccessTokenSigner" when {
    val sampleCode = "sample-oauth-code"

    "getting an authorization code" when {
      def endpoint: MappingBuilder = post(urlMatching("/accounts/api/token"))

      def clientReceivingSuccessfulAccessTokenSigner(
        request: => SttpResponse[DE, AccessTokenSigner]
      ): Unit = {
        "the server responds with the expected json message" should {
          def stub: StubMapping =
            stubFor(
              endpoint
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

          "return a Right with the response Token" in matchResponseBody(stub, request) {
            case Right(authorizationCode) =>
              authorizationCode should matchTo(
                AccessTokenSigner(
                  generatedAt = 1L,
                  accessToken = AccessToken("some-access-token"),
                  tokenType = "Bearer",
                  expiresIn = 3600L,
                  refreshToken = Some(RefreshToken("refresh-token")),
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

      "getting an authorization code from invalid uri response" in {
        val request = sampleClient.auth.AuthorizationCode.acquire(
          SpotifyUserAuthorizationRequest(
            redirectUri = RedirectUri(uri"https://bartholomews.io/callback"),
            scopes = List.empty,
            state = Some("some_state")
          ),
          redirectionUriResponse = uri"https://bartholomews.io/callback?state=some_state"
        )

        inside(request.body) {
          case Left(HttpError(body, status)) =>
            status shouldBe StatusCode.Unauthorized
            body shouldBe "missing_required_query_parameters"
        }
      }

      "getting an authorization code from rejected uri response" in {
        val request = sampleClient.auth.AuthorizationCode.acquire(
          SpotifyUserAuthorizationRequest(
            redirectUri = RedirectUri(uri"https://bartholomews.io/callback"),
            scopes = List.empty,
            state = Some("some_state")
          ),
          redirectionUriResponse = uri"https://bartholomews.io/callback?error=access_denied&state=some_state"
        )

        inside(request.body) {
          case Left(HttpError(body, status)) =>
            status shouldBe StatusCode.Unauthorized
            body shouldBe "access_denied"
        }
      }

      "getting an authorization code from valid uri response" when {
        def request: SttpResponse[DE, AccessTokenSigner] = sampleClient.auth.AuthorizationCode.acquire(
          SpotifyUserAuthorizationRequest(
            redirectUri = RedirectUri(uri"https://bartholomews.io/callback"),
            scopes = List.empty,
            state = Some("some_state")
          ),
          redirectionUriResponse = uri"https://bartholomews.io/callback?code=$sampleCode&state=some_state"
        )

        behave like clientReceivingSuccessfulAccessTokenSigner(request)
        behave like clientReceivingUnexpectedResponse(endpoint, request)
      }
    }

    "getting a refresh token" when {
      def endpoint: MappingBuilder = post(urlMatching("/accounts/api/token"))

      def request: Identity[SttpResponse[DE, AccessTokenSigner]] =
        sampleClient.auth.AuthorizationCode.refresh(sampleRefreshToken)

      "the server responds with the expected json message" should {
        def stub: StubMapping =
          stubFor(
            endpoint
              .withRequestBody(equalTo(s"grant_type=refresh_token&refresh_token=${sampleRefreshToken.value}"))
              .willReturn(
                aResponse()
                  .withStatus(200)
                  .withBodyFile("auth/authorization_access.json")
              )
          )

        "return a Right with the response Token" in matchResponseBody(stub, request) {
          case Right(authorizationCode) =>
            authorizationCode should matchTo(
              AccessTokenSigner(
                generatedAt = 1L,
                accessToken = AccessToken("some-access-token"),
                tokenType = "Bearer",
                expiresIn = 3600L,
                refreshToken = Some(RefreshToken("refresh-token")),
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
      def request: SttpResponse[DE, NonRefreshableTokenSigner] =
        sampleClient.auth.clientCredentials

      def endpoint: MappingBuilder = post(urlMatching("/accounts/api/token"))

      behave like clientReceivingUnexpectedResponse(endpoint, request)

      "the server responds with the expected json message" should {
        def stub: StubMapping =
          stubFor(
            endpoint
              .withRequestBody(equalTo("grant_type=client_credentials"))
              .willReturn(
                aResponse()
                  .withStatus(200)
                  .withBodyFile("auth/client_credentials.json")
              )
          )

        "return a Right with the response Token" in matchResponseBody(stub, request) {
          case Right(token) =>
            token should matchTo(
              NonRefreshableTokenSigner(
                generatedAt = 1L,
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
      val sampleRedirectUri = RedirectUri(uri"https://bartholomews.io/callback")

      "return the correct Uri with default parameters" in {
        val request = sampleClient.auth.authorizeUrl(
          SpotifyUserAuthorizationRequest(
            redirectUri = sampleRedirectUri,
            state = None,
            scopes = List.empty
          )
        )

        request shouldEqual
          uri"http://127.0.0.1:8080/accounts/authorize"
            .withQueryParam("show_dialog", false.toString)
            .withQueryParam("client_id", "SAMPLE_CLIENT_ID")
            .withQueryParam("response_type", "code")
            .withQueryParam(
              "redirect_uri",
              sampleRedirectUri.value.toString
            )
      }

      "return the correct Uri with explicit parameters" in {
        val request = sampleClient.auth.authorizeUrl(
          SpotifyUserAuthorizationRequest(
            redirectUri = sampleRedirectUri,
            state = Some("1010101010101010101"),
            scopes = List(SpotifyScope.PLAYLIST_MODIFY_PRIVATE, SpotifyScope.PLAYLIST_READ_PRIVATE)
          ),
          showDialog = true
        )

        request shouldEqual
          uri"http://127.0.0.1:8080/accounts/authorize"
            .withQueryParam("show_dialog", true.toString)
            .withQueryParam("client_id", "SAMPLE_CLIENT_ID")
            .withQueryParam("response_type", "code")
            .withQueryParam("redirect_uri", sampleRedirectUri.value.toString)
            .withQueryParam("state", "1010101010101010101")
            .withQueryParam(
              "scope",
              "playlist-modify-private playlist-read-private"
            )
      }
    }
  }
}
