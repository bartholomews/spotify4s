package io.bartholomews.spotify4s.core.api

import cats.Applicative
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.{ResponseHandler, SttpResponse}
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2._
import io.bartholomews.fsclient.core.oauth.v2.{AuthorizationCodeRequest, ClientPassword}
import io.bartholomews.fsclient.core.oauth.{
  AccessTokenSigner,
  ClientPasswordAuthentication,
  NonRefreshableTokenSigner,
  RedirectUri
}
import io.bartholomews.spotify4s.core.api.AuthApi.SpotifyUserAuthorizationRequest
import io.bartholomews.spotify4s.core.api.SpotifyApi.accountsUri
import io.bartholomews.spotify4s.core.entities.SpotifyScope
import sttp.client3.{HttpError, Response, ResponseException}
import sttp.model.Uri.{PathSegment, QuerySegment}
import sttp.model.{StatusCode, Uri}

// https://developer.spotify.com/documentation/general/guides/authorization-guide
private[spotify4s] class AuthApi[F[_]](client: FsClient[F, ClientPasswordAuthentication]) {
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  private val clientPassword = client.signer.clientPassword
  private val tokenEndpoint = accountsUri / "api" / "token"

  /**
    * @param request the SpotifyUserAuthorizationRequest
    * @param showDialog Whether or not to force the user to approve the app again if they’ve already done so.
    *                   If false (default), a user who has already approved the application
    *                   may be automatically redirected to the URI specified by redirect_uri.
    *                   If true, the user will not be automatically redirected and will have to approve the app again.
    *
    * @return the Uri where the user will grant/deny the app permissions
    */
  def authorizeUrl(
    request: SpotifyUserAuthorizationRequest,
    showDialog: Boolean = false
  ): Uri = {
    val serverUri =
      accountsUri
        .addPathSegment(PathSegment("authorize"))
        .addQuerySegment(QuerySegment.KeyValue("show_dialog", showDialog.toString))

    AuthorizationCodeGrant
      .authorizationRequestUri(request.toAuthorizationCodeRequest(clientPassword), serverUri)
  }

  // https://developer.spotify.com/documentation/general/guides/authorization-guide/#authorization-code-flow
  // TODO[FB] ~ Might be worth to distinguish between response types:
  //  `acquire` (has refresh token) vs `refresh` (doesn't have refresh token, need to use the acquired one)
  object AuthorizationCode {
    /**
      *
      * @param request the original authorization request
      * @param redirectionUriResponse the url where the user is redirected after approving/denying app permissions
      * @param responseHandler the sttp response handler
      * @param f the effect with Applicative capabilities
      * @tparam DE the deserialization error
      * @return
      */
    def acquire[DE](
      request: SpotifyUserAuthorizationRequest,
      redirectionUriResponse: Uri
    )(
      implicit responseHandler: ResponseHandler[DE, AccessTokenSigner],
      f: Applicative[F]
    ): F[SttpResponse[DE, AccessTokenSigner]] =
      AuthorizationCodeGrant
        .authorizationResponse(request.toAuthorizationCodeRequest(clientPassword), redirectionUriResponse)
        .fold(
          errorMsg =>
            f.pure(
              Response.apply[Either[ResponseException[String, DE], AccessTokenSigner]](
                // Consider having a DeserializationError in fsclient instead
                body = Left(HttpError(errorMsg, StatusCode.Unauthorized)),
                code = StatusCode.Unauthorized
              )
            ),
          verifier =>
            client.backend.send(
              AuthorizationCodeGrant
                .accessTokenRequest(tokenEndpoint, verifier, Some(request.redirectUri), clientPassword)
            )
        )

    def refresh[DE](
      refreshToken: RefreshToken
    )(implicit responseHandler: ResponseHandler[DE, AccessTokenSigner]): F[SttpResponse[DE, AccessTokenSigner]] =
      AuthorizationCodeGrant
        .refreshTokenRequest(tokenEndpoint, refreshToken, scopes = List.empty, clientPassword)
        .send(client.backend)
  }

  /*
    This flow is exposed in `SpotifySimpleClient` which will also manage token re-fetching
    https://developer.spotify.com/documentation/general/guides/authorization-guide/#client-credentials-flow
   */
  private[spotify4s] def clientCredentials[DE](
    implicit responseHandler: ResponseHandler[DE, NonRefreshableTokenSigner]
  ): F[SttpResponse[DE, NonRefreshableTokenSigner]] =
    ClientCredentialsGrant
      .accessTokenRequest(tokenEndpoint, clientPassword)
      .send(client.backend)
}

object AuthApi {
  case class SpotifyUserAuthorizationRequest(
    redirectUri: RedirectUri,
    scopes: List[SpotifyScope],
    state: Option[String]
  ) {
    def toAuthorizationCodeRequest(clientPassword: ClientPassword): AuthorizationCodeRequest =
      AuthorizationCodeRequest(
        clientId = clientPassword.clientId,
        redirectUri = redirectUri,
        state = state,
        scopes = scopes.map(_.entryName)
      )
  }
}
