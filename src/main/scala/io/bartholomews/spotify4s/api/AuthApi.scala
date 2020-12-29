package io.bartholomews.spotify4s.api

import cats.Applicative
import io.bartholomews.fsclient.core.http.SttpResponses.CirceJsonResponse
import io.bartholomews.fsclient.core.oauth.v2.AuthorizationCodeRequest
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.{
  AuthorizationCodeGrant,
  ClientCredentialsGrant,
  RedirectUri,
  RefreshToken,
  ResponseHandler
}
import io.bartholomews.fsclient.core.oauth.{AccessTokenSigner, ClientPasswordAuthentication, NonRefreshableTokenSigner}
import io.bartholomews.fsclient.core.{FsApiClient, FsClient}
import io.bartholomews.spotify4s.api.SpotifyApi._
import io.bartholomews.spotify4s.entities.SpotifyScope
import sttp.client.circe.asJson
import sttp.model.Uri.{PathSegment, QuerySegment}
import sttp.model.{StatusCode, Uri}

class AuthApi[F[_]](client: FsClient[F, ClientPasswordAuthentication])
    extends FsApiClient[F, ClientPasswordAuthentication](client) {
  import io.circe._
  import sttp.client._

  private val clientPassword = client.signer.clientPassword

  private val tokenEndpoint = accountsUri / "api" / "token"

  def authorizationCodeRequest(
    redirectUri: RedirectUri,
    state: Option[String],
    scopes: List[SpotifyScope]
  ): AuthorizationCodeRequest =
    AuthorizationCodeRequest(
      clientId = clientPassword.clientId,
      redirectUri = redirectUri,
      state = state,
      scopes = scopes.map(_.entryName)
    )

  /**
    *
    * @param redirectUri
    *                    The URI to redirect to after the user grants or denies permission.
    *                    This URI needs to have been entered in the Redirect URI whitelist
    *                    that you specified when you registered your application.
    *                    The value of redirect_uri here must exactly match one of the values you entered
    *                    when you registered your application,
    *                    including upper or lowercase, terminating slashes, and such.
    *
    * @param state
    *              Optional, but strongly recommended.
    *              The state can be useful for correlating requests and responses.
    *              Because your redirect_uri can be guessed,
    *              using a state value can increase your assurance that an incoming connection
    *              is the result of an authentication request.
    *              If you generate a random string, or encode the hash of some client state,
    *              such as a cookie, in this state variable, you can validate the response
    *              to additionally ensure that both the request and response originated in the same browser.
    *              This provides protection against attacks such as cross-site request forgery.
    *              See RFC-6749 (https://tools.ietf.org/html/rfc6749#section-4.1).
    *
    * @param scopes
    *               A space-separated list of scopes.
    *               If no scopes are specified, authorization will be granted
    *               only to access publicly available information:
    *               that is, only information normally visible in the Spotify desktop, web, and mobile players.
    *
    * @param showDialog
    *                   Whether or not to force the user to approve the app again if they’ve already done so.
    *                   If false (default), a user who has already approved the application may be
    *                   automatically redirected to the URI specified by redirect_uri.
    *                   If true, the user will not be automatically redirected and will have to approve the app again.
    * @return
    */
  def authorizeUrl(
    authorizationCodeRequest: AuthorizationCodeRequest,
    showDialog: Boolean = false
  ): Uri = {
    val serverUri =
      accountsUri
        .pathSegments(accountsUri.pathSegments ++ List(PathSegment("authorize")))
        .querySegment(QuerySegment.KeyValue("show_dialog", showDialog.toString))

    AuthorizationCodeGrant
      .authorizationRequestUri(authorizationCodeRequest, serverUri)
  }

  // https://developer.spotify.com/documentation/general/guides/authorization-guide/#authorization-code-flow
  object AuthorizationCode {
    def getAccessToken(request: AuthorizationCodeRequest, redirectionUriResponse: Uri)(
      implicit f: Applicative[F]
    ): F[Response[Either[ResponseError[Error], AccessTokenSigner]]] = {
      AuthorizationCodeGrant
        .authorizationResponse(request, redirectionUriResponse)
        .fold(
          // FIXME: ResponseError[T] for non-circe parsers ?
          errorMsg =>
            f.pure(
              Response.apply[Either[ResponseError[Error], AccessTokenSigner]](
                body = Left(HttpError(errorMsg, StatusCode.Unauthorized)),
                code = StatusCode.Unauthorized
              )
            ),
          verifier => {
            implicit val responseHandler: ResponseHandler[AccessTokenSigner] = asJson[AccessTokenSigner]

            AuthorizationCodeGrant
              .accessTokenRequest(
                tokenEndpoint,
                verifier,
                Some(request.redirectUri),
                clientPassword
              )
              .send()
          }
        )
    }

    def getRefreshToken(refreshToken: RefreshToken): F[CirceJsonResponse[AccessTokenSigner]] = {
      implicit val responseHandler: ResponseHandler[AccessTokenSigner] = asJson[AccessTokenSigner]
      AuthorizationCodeGrant
        .refreshTokenRequest(tokenEndpoint, refreshToken, scopes = List.empty, clientPassword)
        .send()
    }
  }

  // https://developer.spotify.com/documentation/general/guides/authorization-guide/#client-credentials-flow
  def clientCredentials(): F[CirceJsonResponse[NonRefreshableTokenSigner]] = {
    implicit val responseHandler: ResponseHandler[NonRefreshableTokenSigner] = asJson[NonRefreshableTokenSigner]
    ClientCredentialsGrant
      .accessTokenRequest(tokenEndpoint, clientPassword)
      .send()
  }
}
