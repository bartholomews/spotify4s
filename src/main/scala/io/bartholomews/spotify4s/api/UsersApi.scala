package io.bartholomews.spotify4s.api

import cats.effect.ConcurrentEffect
import fs2.Pipe
import io.bartholomews.fsclient.client.FsClient
import io.bartholomews.fsclient.entities.oauth.{Signer, SignerV2}
import io.bartholomews.fsclient.requests.FsAuthJson
import io.bartholomews.fsclient.utils.HttpTypes.HttpResponse
import io.bartholomews.spotify4s.api.SpotifyApi.{apiUri, Offset}
import io.bartholomews.spotify4s.entities.{Page, PrivateUser, SimplePlaylist}
import io.circe.Json
import org.http4s.Uri

// https://developer.spotify.com/documentation/web-api/reference/users-profile/
class UsersApi[F[_]: ConcurrentEffect, S <: Signer](client: FsClient[F, S]) {
  import eu.timepit.refined.auto.autoRefineV
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
    new FsAuthJson.Get[PrivateUser] {
      override val uri: Uri = basePath / "me"
    }.runWith(client)

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
  def getPlaylists(limit: SimplePlaylist.Limit = 20, offset: Offset = 0)(
    implicit signer: SignerV2
  ): F[HttpResponse[Page[SimplePlaylist]]] = {
    implicit val pipeDecoder: Pipe[F, Json, Page[SimplePlaylist]] = Page.pipeDecoder
    new FsAuthJson.Get[Page[SimplePlaylist]] {
      override val uri: Uri = (basePath / "me" / "playlists")
        .withQueryParam("limit", limit.value)
        .withQueryParam("offset", offset)
    }.runWith(client)
  }
}
