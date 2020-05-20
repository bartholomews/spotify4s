package io.bartholomews.spotify4s.api

import cats.effect.ConcurrentEffect
import io.bartholomews.fsclient.client.FsClient
import io.bartholomews.fsclient.entities.oauth.{Signer, SignerV2}
import io.bartholomews.fsclient.requests.AuthJsonRequest
import io.bartholomews.fsclient.utils.HttpTypes.HttpResponse
import io.bartholomews.spotify4s.api.SpotifyApi.apiUri
import io.bartholomews.spotify4s.entities.PrivateUser
import org.http4s.Uri

// https://developer.spotify.com/documentation/web-api/reference/users-profile/
class UsersApi[F[_]: ConcurrentEffect, S <: Signer](client: FsClient[F, S]) {
  import io.bartholomews.fsclient.implicits.{emptyEntityEncoder, rawJsonPipe}
  private[api] val basePath: Uri = apiUri / "v1"

  /**
    * https://developer.spotify.com/documentation/web-api/reference/users-profile/
    *
    * If the user-read-email scope is authorized,
    * the returned JSON will include the email address that was entered when the user created their Spotify account.
    * This email address is unverified; do not assume that the email address belongs to the user.
    *
    * @param signer Required. A valid access token from the Spotify Accounts service:
    *               see the Web API Authorization Guide for details.
    *               The access token must have been issued on behalf of the current user.
    *               Reading the user’s email address requires the user-read-email scope;
    *               reading country and product subscription level requires the user-read-private scope.
    *               See Using Scopes.
    *
    * @return On success, the HTTP status code in the response header is 200 OK
    *          and the response body contains a user object in JSON format.
    *          On error, the header status code is an error code and the response body contains an error object.
    *          When requesting fields that you don’t have the user’s authorization to access,
    *          it will return error 403 Forbidden.
    */
  def me(implicit signer: SignerV2): F[HttpResponse[PrivateUser]] =
    new AuthJsonRequest.Get[PrivateUser] {
      override val uri: Uri = basePath / "me"
    }.runWith(client)
}
