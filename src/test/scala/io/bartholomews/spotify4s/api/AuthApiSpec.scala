//package io.bartholomews.spotify4s.api
//
//import com.github.tomakehurst.wiremock.client.MappingBuilder
//import com.github.tomakehurst.wiremock.client.WireMock._
//import com.github.tomakehurst.wiremock.stubbing.StubMapping
//import io.bartholomews.fsclient.core.http.FsClientSttpExtensions.UriExtensions
//import io.bartholomews.fsclient.core.http.SttpResponses.{CirceJsonResponse, SttpResponse}
//import io.bartholomews.fsclient.core.oauth.{AccessTokenSigner, NonRefreshableTokenSigner, Scope}
//import io.bartholomews.scalatestudo.WireWordSpec
//import io.bartholomews.spotify4s.entities.SpotifyScope
//import io.circe
//import sttp.client.{HttpError, Response, ResponseError, UriContext}
//import sttp.model.StatusCode
//
//// http://blog.shangjiaming.com/2018/01/04/http4s-intorduction/
//// https://www.lewuathe.com/wiremock-in-scala.html
//class AuthApiSpec extends WireWordSpec with ServerBehaviours {
//  import io.bartholomews.spotify4s.client.ClientData._
//
//  "AccessTokenSigner" when {
//    val sampleCode = "sample-oauth-code"
//
//    "getting an authorization code" when {
//      def endpoint: MappingBuilder = post(urlMatching("/accounts/api/token"))
//
//      def clientReceivingSuccessfulAccessTokenSigner[E <: ResponseError[_]](
//        request: => SttpResponse[E, AccessTokenSigner]
//      ): Unit = {
//        "the server responds with the expected json message" should {
//          def stub: StubMapping =
//            stubFor(
//              endpoint
//                .withRequestBody(
//                  equalTo(
//                    "grant_type=authorization_code&code=sample-oauth-code&redirect_uri=https%3A%2F%2Fbartholomews.io%2Fcallback"
//                  )
//                )
//                .willReturn(
//                  aResponse()
//                    .withStatus(200)
//                    .withBodyFile("auth/authorization_access.json")
//                )
//            )
//
//          "return a Right with the response Token" in matchResponseBody(stub, request) {
//            case Right(authorizationCode) =>
//              authorizationCode should matchTo(
//                AccessTokenSigner(
//                  generatedAt = 1L,
//                  accessToken = AccessToken("some-access-token"),
//                  tokenType = "Bearer",
//                  expiresIn = 3600L,
//                  refreshToken = Some(RefreshToken("refresh-token")),
//                  scope = Scope(List("user-read-private", "user-read-email", "unknown-scope"))
//                )
//              )
//              SpotifyScope.fromScope(authorizationCode.scope) shouldBe List(
//                SpotifyScope.USER_READ_PRIVATE,
//                SpotifyScope.USER_READ_EMAIL
//              )
//          }
//        }
//      }
//
//      "getting an authorization code from code and redirect uri" when {
//        def request: CirceJsonResponse[AccessTokenSigner] = sampleClient.auth.AuthorizationCode.getAccessToken(
//          code = sampleCode,
//          redirectUri = RedirectUri(uri"https://bartholomews.io/callback")
//        )
//
//        behave like clientReceivingSuccessfulAccessTokenSigner(request)
//        behave like clientReceivingUnexpectedResponse(endpoint, request)
//      }
//
//      "getting an authorization code from invalid uri response" in {
//        val request = sampleClient.auth.getAccessToken(
//          uri"https://bartholomews.io/callback?state=some_state"
//        )
//
//        inside(request.body) {
//          case Left(HttpError(body, status)) =>
//            status shouldBe StatusCode.Unauthorized
//            body shouldBe "missing_required_query_parameters"
//        }
//      }
//
//      "getting an authorization code from rejected uri response" in {
//        val request = sampleClient.auth.AccessTokenSigner.getAccessToken(
//          uri"https://bartholomews.io/callback?error=access_denied&state=some_state"
//        )
//
//        inside(request.body) {
//          case Left(HttpError(body, status)) =>
//            status shouldBe StatusCode.Unauthorized
//            body shouldBe "access_denied"
//        }
//      }
//
//      "getting an authorization code from valid uri response" when {
//        def request: Response[Either[ResponseError[circe.Error], AccessTokenSigner]] =
//          sampleClient.auth.AccessTokenSigner.getAccessToken(
//            uri"https://bartholomews.io/callback?code=$sampleCode&state=some_state"
//          )
//
//        behave like clientReceivingSuccessfulAccessTokenSigner(request)
//        behave like clientReceivingUnexpectedResponse(endpoint, request)
//      }
//    }
//
//    "getting a refresh token" when {
//      def endpoint: MappingBuilder = post(urlMatching("/accounts/api/token"))
//
//      def request: CirceJsonResponse[AccessTokenSigner] = sampleClient.auth.AccessTokenSigner.getRefreshToken(
//        sampleRefreshToken
//      )
//
//      "the server responds with the expected json message" should {
//        def stub: StubMapping =
//          stubFor(
//            endpoint
//              .withRequestBody(equalTo(s"grant_type=refresh_token&refresh_token=${sampleRefreshToken.value}"))
//              .willReturn(
//                aResponse()
//                  .withStatus(200)
//                  .withBodyFile("auth/authorization_access.json")
//              )
//          )
//
//        "return a Right with the response Token" in matchResponseBody(stub, request) {
//          case Right(authorizationCode) =>
//            authorizationCode should matchTo(
//              AccessTokenSigner(
//                generatedAt = 1L,
//                accessToken = AccessToken("some-access-token"),
//                tokenType = "Bearer",
//                expiresIn = 3600L,
//                refreshToken = Some(RefreshToken("refresh-token")),
//                scope = Scope(List("user-read-private", "user-read-email", "unknown-scope"))
//              )
//            )
//            SpotifyScope.fromScope(authorizationCode.scope) shouldBe List(
//              SpotifyScope.USER_READ_PRIVATE,
//              SpotifyScope.USER_READ_EMAIL
//            )
//        }
//      }
//    }
//
//    "clientCredentials" when {
//      def request: CirceJsonResponse[NonRefreshableTokenSigner] = sampleClient.auth.clientCredentials()
//
//      def endpoint: MappingBuilder = post(urlMatching("/accounts/api/token"))
//
//      behave like clientReceivingUnexpectedResponse(endpoint, request)
//
//      "the server responds with the expected json message" should {
//        def stub: StubMapping =
//          stubFor(
//            endpoint
//              .withRequestBody(equalTo("grant_type=client_credentials"))
//              .willReturn(
//                aResponse()
//                  .withStatus(200)
//                  .withBodyFile("auth/client_credentials.json")
//              )
//          )
//
//        "return a Right with the response Token" in matchResponseBody(stub, request) {
//          case Right(token) =>
//            token should matchTo(
//              NonRefreshableTokenSigner(
//                generatedAt = 1L,
//                accessToken = AccessToken("some-access-token"),
//                tokenType = "bearer",
//                expiresIn = 3600L,
//                scope = Scope(List.empty)
//              )
//            )
//        }
//      }
//    }
//
//    "authorizeUrl" should {
//      val sampleRedirectUri = uri"https://bartholomews.io/callback"
//
//      "return the correct Uri with default parameters" in {
//        val request = sampleClient.auth.authorizeUrl(
//          redirectUri = sampleRedirectUri,
//          state = None,
//          scopes = List.empty
//        )
//
//        request should matchTo(
//          uri"http://127.0.0.1:8080/accounts/authorize"
//            .withQueryParam("show_dialog", false.toString)
//            .withQueryParam("redirect_uri", sampleRedirectUri.toString())
//            .withQueryParam("client_id", "SAMPLE_CLIENT_ID")
//            .withQueryParam("response_type", "code")
//        )
//      }
//
//      "return the correct Uri with explicit parameters" in {
//        val request = sampleClient.auth.authorizeUrl(
//          redirectUri = sampleRedirectUri,
//          state = Some("1010101010101010101"),
//          scopes = List(SpotifyScope.PLAYLIST_MODIFY_PRIVATE, SpotifyScope.PLAYLIST_READ_PRIVATE),
//          showDialog = true
//        )
//
//        println(request.toString())
//
//        request should matchTo(
//          uri"http://127.0.0.1:8080/accounts/authorize"
//            .withQueryParam("show_dialog", true.toString)
//            .withQueryParam("state", "1010101010101010101")
//            .withQueryParam("scope", "playlist-modify-private playlist-read-private")
//            .withQueryParam("redirect_uri", sampleRedirectUri.toString())
//            .withQueryParam("client_id", "SAMPLE_CLIENT_ID")
//            .withQueryParam("response_type", "code")
//        )
//      }
//    }
//  }
//}
