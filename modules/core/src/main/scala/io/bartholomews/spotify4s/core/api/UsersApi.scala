package io.bartholomews.spotify4s.core.api

import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.ResponseHandler
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.spotify4s.core.api.SpotifyApi.{apiUri, Offset}
import io.bartholomews.spotify4s.core.entities.{Page, PrivateUser, SimplePlaylist}
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/users-profile
private[spotify4s] class UsersApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import eu.timepit.refined.auto.autoRefineV
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

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
    * On success, the HTTP status code in the response header is 200 OK
    * and the response body contains a user object in JSON format.
    * On error, the header status code is an error code and the response body contains an error object.
    * When requesting fields that you don’t have the user’s authorization to access,
    * it will return error 403 Forbidden.
    */
  def me[E](
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[E, PrivateUser]): F[SttpResponse[E, PrivateUser]] =
    baseRequest(client.userAgent)
      .get(basePath / "me")
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)

  /**
    * https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-get-a-list-of-current-users-playlists
    *
    * @param limit  The maximum number of playlists to return. Default: 20. Minimum: 1. Maximum: 50.
    *
    * @param offset The index of the first playlist to return.
    *                Default: 0 (the first object). Maximum offset: 100.000.
    *                Use with limit to get the next set of playlists.
    *
    *  @param signer A valid access token from the Spotify Accounts service:
    *                Private playlists are only retrievable for the current user
    *                and requires the playlist-read-private scope to have been authorized by the user.
    *                Note that this scope alone will not return collaborative playlists,
    *                even though they are always private.
    *                Collaborative playlists are only retrievable for the current user
    *                and requires the playlist-read-collaborative scope to have been authorized by the user.
    *
    *  @return  On success, the HTTP status code in the response header is 200 OK
    *            and the response body contains an array of simplified playlist objects
    *            (wrapped in a paging object) in JSON format.
    *            On error, the header status code is an error code and the response body contains an error object.
    */
  def getPlaylists[E](limit: SimplePlaylist.Limit = 20, offset: Offset = 0)(signer: SignerV2)(
    implicit responseHandler: ResponseHandler[E, Page[SimplePlaylist]]
  ): F[SttpResponse[E, Page[SimplePlaylist]]] = {
    val uri: Uri = (basePath / "me" / "playlists")
      .withQueryParam("limit", limit.value.toString)
      .withQueryParam("offset", offset.toString)

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }
}
