package io.bartholomews.spotify4s.api

import cats.Applicative
import cats.effect.ConcurrentEffect
import io.bartholomews.fsclient.client.FsClientV2
import io.bartholomews.fsclient.entities.oauth.v2.OAuthV2AuthorizationFramework._
import io.bartholomews.fsclient.entities.oauth.{AuthorizationCode, NonRefreshableToken, SignerV2}
import io.bartholomews.fsclient.entities.{ErrorBodyString, FsResponse}
import io.bartholomews.fsclient.utils.HttpTypes.HttpResponse
import io.bartholomews.spotify4s.api.SpotifyApi.accountsUri
import io.bartholomews.spotify4s.entities.SpotifyScope
import org.http4s.{Headers, Status, Uri}

// https://developer.spotify.com/documentation/general/guides/authorization-guide
class AuthApi[F[_]: ConcurrentEffect](client: FsClientV2[F, SignerV2]) {
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
    redirectUri: Uri,
    state: Option[String],
    scopes: List[SpotifyScope],
    showDialog: Boolean = false
  ): Uri = {
    AuthorizationCodeGrant
      .authorizationCodeUri(client.clientPassword.clientId, redirectUri, state, scopes.map(_.entryName)) {
        (accountsUri / "authorize")
          .withQueryParam("show_dialog", showDialog)
      }
  }

  // https://developer.spotify.com/documentation/general/guides/authorization-guide/#authorization-code-flow
  object AuthorizationCode {
    private val tokenEndpoint = accountsUri / "api" / "token"

    def apply(code: String, redirectUri: RedirectUri): F[HttpResponse[AuthorizationCode]] = {
      implicit val signer: SignerV2 = client.appConfig.signer
      new AuthorizationCodeGrant.AccessTokenRequest(code, Some(redirectUri)) {
        override val uri: Uri = tokenEndpoint
      }.runWith(client)
    }

    def refresh(refreshToken: RefreshToken): F[HttpResponse[AuthorizationCode]] = {
      implicit val signer: SignerV2 = client.appConfig.signer
      new AuthorizationCodeGrant.RefreshTokenRequest(refreshToken, scope = List.empty) {
        override val uri: Uri = tokenEndpoint
      }.runWith(client)
    }

    /**
      *
      * @param uri if the user does not accept your request or an error has occurred,
      *            the response query string, for example https://example.com/callback?error=access_denied&state=STATE,
      *            contains the following parameters:
      *            error 	The reason authorization failed, for example: “access_denied”
      *            state 	The value of the state parameter supplied in the request.
      *             TODO: prob worth to validate `state` if present
      *
      * @param f   `Applicative[F]` must be in scope
      *
      * @return an `FsResponse[HttpError, AuthorizationCode]` wrapped in the ConcurrentEffect `F`
      */
    def fromUri(uri: Uri)(implicit f: Applicative[F]): F[HttpResponse[AuthorizationCode]] = {
      val response: Either[String, String] = uri.query.pairs
        .collectFirst({
          case ("code", Some(code)) => Right(code)
          case ("error", Some(error)) => Left(error)
        })
        .toRight("missing_required_query_parameters")
        .joinRight

      val redirectUri = RedirectUri(Uri(uri.scheme, uri.authority, uri.path.dropEndsWithSlash))
      response.fold(
        errorMsg => f.pure(FsResponse(Headers.empty, Status.Unauthorized, Left(ErrorBodyString(errorMsg)))),
        code => apply(code, redirectUri)
      )
    }
  }

  // https://developer.spotify.com/documentation/general/guides/authorization-guide/#client-credentials-flow
  def clientCredentials(): F[HttpResponse[NonRefreshableToken]] = {
    implicit val signer: SignerV2 = client.appConfig.signer
    new ClientCredentialsGrant.AccessTokenRequest() {
      override val uri: Uri = accountsUri / "api" / "token"
    }.runWith(client)
  }
}
