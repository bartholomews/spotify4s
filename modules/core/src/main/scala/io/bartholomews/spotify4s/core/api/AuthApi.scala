package io.bartholomews.spotify4s.core.api

import cats.Applicative
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.{
  AuthorizationCodeGrant,
  ClientCredentialsGrant,
  RedirectUri,
  RefreshToken,
  ResponseHandler
}
import io.bartholomews.fsclient.core.oauth.v2.{AuthorizationCodeRequest, ClientPassword}
import io.bartholomews.fsclient.core.oauth.{AccessTokenSigner, ClientPasswordAuthentication, NonRefreshableTokenSigner}
import io.bartholomews.fsclient.core.{FsApiClient, FsClient}
import io.bartholomews.spotify4s.core.api.AuthApi.SpotifyUserAuthorizationRequest
import io.bartholomews.spotify4s.core.api.SpotifyApi.accountsUri
import io.bartholomews.spotify4s.core.entities.SpotifyScope
import sttp.client3.{HttpError, Response, ResponseException}
import sttp.model.Uri.{PathSegment, QuerySegment}
import sttp.model.{StatusCode, Uri}

class AuthApi[F[_]](client: FsClient[F, ClientPasswordAuthentication])
    extends FsApiClient[F, ClientPasswordAuthentication](client) {
  private val clientPassword = client.signer.clientPassword
  private val tokenEndpoint = accountsUri / "api" / "token"

  def authorizeUrl(
    request: SpotifyUserAuthorizationRequest,
    showDialog: Boolean = false
  ): Uri = {
    val serverUri =
      accountsUri
        .withPathSegments(accountsUri.pathSegments ++ List(PathSegment("authorize")))
        .addQuerySegment(QuerySegment.KeyValue("show_dialog", showDialog.toString))

    AuthorizationCodeGrant
      .authorizationRequestUri(request.toAuthorizationCodeRequest(clientPassword), serverUri)
  }

  // https://developer.spotify.com/documentation/general/guides/authorization-guide/#authorization-code-flow
  object AuthorizationCode {
    def acquire[DE](
      request: SpotifyUserAuthorizationRequest,
      redirectionUriResponse: Uri
    )(
      implicit f: Applicative[F],
      responseHandler: ResponseHandler[DE, AccessTokenSigner]
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
            backend.send(
              AuthorizationCodeGrant
                .accessTokenRequest(tokenEndpoint, verifier, Some(request.redirectUri), clientPassword)
            )
        )

    def refresh[E](
      refreshToken: RefreshToken
    )(implicit responseHandler: ResponseHandler[E, AccessTokenSigner]): F[SttpResponse[E, AccessTokenSigner]] =
      backend.send(
        AuthorizationCodeGrant
          .refreshTokenRequest(tokenEndpoint, refreshToken, scopes = List.empty, clientPassword)
      )
  }

  // https://developer.spotify.com/documentation/general/guides/authorization-guide/#client-credentials-flow
  def clientCredentials[E](
    implicit responseHandler: ResponseHandler[E, NonRefreshableTokenSigner]
  ): F[SttpResponse[E, NonRefreshableTokenSigner]] =
    backend.send(
      ClientCredentialsGrant
        .accessTokenRequest(tokenEndpoint, clientPassword)
    )
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