package io.bartholomews.spotify4s.api

import cats.effect.ConcurrentEffect
import io.bartholomews.fsclient.client.FsClient
import io.bartholomews.fsclient.entities.oauth.{Signer, SignerV2}
import io.bartholomews.fsclient.requests.FsAuthJson
import io.bartholomews.fsclient.utils.HttpTypes.HttpResponse
import io.bartholomews.spotify4s.api.SpotifyApi.apiUri
import io.bartholomews.spotify4s.entities.SpotifyId
import org.http4s.Uri

// https://developer.spotify.com/documentation/web-api/reference/browse/
class FollowApi[F[_]: ConcurrentEffect, S <: Signer](client: FsClient[F, S]) {
  private[api] val basePath: Uri = apiUri / "v1"

  /**
    * Unfollow Playlist
    * https://developer.spotify.com/documentation/web-api/reference/follow/unfollow-playlist
    *
    * @param playlistId The Spotify ID of the playlist that is to be no longer followed.
    * @param signer Required. A valid access token from the Spotify Accounts service:
    *               see the Web API Authorization Guide for details.
    *               The access token must have been issued on behalf of the user.
    *               Unfollowing a publicly followed playlist for a user
    *               requires authorization of the playlist-modify-public scope;
    *               unfollowing a privately followed playlist
    *               requires the playlist-modify-private scope. See Using Scopes.
    *               Note that the scopes you provide relate only to whether
    *               the current user is following the playlist publicly or privately
    *               (i.e. showing others what they are following),
    *               not whether the playlist itself is public or private.
    *
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body is empty. On error,
    *         the header status code is an error code
    *         and the response body contains an error object.
    */
  def unfollowPlaylist(playlistId: SpotifyId)(
    implicit signer: SignerV2
  ): F[HttpResponse[Unit]] =
    new FsAuthJson.DeleteEmpty[Unit] {
      override val uri: Uri = basePath / "playlists" / playlistId.value / "followers"
    }.runWith(client)
}
