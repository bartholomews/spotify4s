package io.bartholomews.spotify4s.core.api

import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.{ResponseHandler, SttpResponse}
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.spotify4s.core.api.SpotifyApi.apiUri
import io.bartholomews.spotify4s.core.entities.SpotifyId.SpotifyUserId
import io.bartholomews.spotify4s.core.entities.{PrivateUser, PublicUser}
import sttp.client3.{Response, ResponseException}
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/users-profile
private[spotify4s] class UsersApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  private[api] val basePath: Uri = apiUri / "v1"

  /**
    * Get Current User's Profile
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-current-users-profile
    * Get detailed profile information about the current user (including the current user’s username).
    *
    * Important! If the user-read-email scope is authorized, the returned JSON will include the email address
    * that was entered when the user created their Spotify account.
    * This email address is unverified; do not assume that the email address belongs to the user.
    *
    * @param signer A valid access token from the Spotify Accounts service: see the Web API Authorization Guide for details.
    *               The access token must have been issued on behalf of the current user.
    *               Reading the user’s email address requires the user-read-email scope;
    *               reading country, product subscription level and explicit content settings
    *               requires the user-read-private scope. See Using Scopes.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK and the response body contains a user object in JSON format.
    * On error, the header status code is an error code and the response body contains an error object.
    * When requesting fields that you don’t have the user’s authorization to access, it will return error 403 Forbidden.
    */
  def me[DE](
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, PrivateUser]): F[SttpResponse[DE, PrivateUser]] =
    baseRequest(client.userAgent)
      .get(basePath / "me")
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)

  /**
    * Get a User's Profile
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-users-profile
    * Get public profile information about a Spotify user.
    *
    * @param userId The user’s Spotify user ID.
    * @param signer A valid access token from the Spotify Accounts service: see the Web API Authorization Guide for details.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains a user object in JSON format.
    *         On error, the header status code is an error code and the response body contains an error object.
    *         If a user with that user_id doesn't exist, the status code is 404 NOT FOUND.
    */
  def getUserProfile[DE](userId: SpotifyUserId)(signer: SignerV2)(
    implicit responseHandler: ResponseHandler[DE, PublicUser]
  ): F[Response[Either[ResponseException[String, DE], PublicUser]]] =
    baseRequest(client.userAgent)
      .get(basePath / "users" / userId.value)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
}
