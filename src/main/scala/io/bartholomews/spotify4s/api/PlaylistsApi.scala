package io.bartholomews.spotify4s.api

import cats.effect.ConcurrentEffect
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval
import fs2.Pipe
import io.bartholomews.fsclient.client.FsClient
import io.bartholomews.fsclient.entities.oauth.{Signer, SignerV2}
import io.bartholomews.fsclient.requests.AuthJsonRequest
import io.bartholomews.fsclient.utils.HttpTypes.HttpResponse
import io.bartholomews.spotify4s.api.SpotifyApi.apiUri
import io.bartholomews.spotify4s.entities.{FullPlaylist, Market, Page, SimplePlaylist, SpotifyId, SpotifyUserId}
import io.circe.{Decoder, Json}
import org.http4s.Uri

// https://developer.spotify.com/console/playlists/
class PlaylistsApi[F[_]: ConcurrentEffect, S <: Signer](client: FsClient[F, S]) {
  import eu.timepit.refined.auto.autoRefineV
  import io.bartholomews.fsclient.implicits.{deriveJsonPipe, emptyEntityEncoder, rawJsonPipe}

  private[api] val basePath: Uri = apiUri / "v1"

  type Limit = Int Refined Interval.Closed[1, 50]
  type Offset = Int Refined Interval.Closed[0, 100]

  /**
    * https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-get-list-users-playlists
    *
    * @param userId The user’s Spotify user ID.
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
  def getUserPlaylists(userId: SpotifyUserId, limit: Limit = 20, offset: Offset = 0)(
    implicit signer: SignerV2
  ): F[HttpResponse[Page[SimplePlaylist]]] = {
    implicit val pipeDecoder: Pipe[F, Json, Page[SimplePlaylist]] = Page.pipeDecoder
    new AuthJsonRequest.Get[Page[SimplePlaylist]] {
      override val uri: Uri = (basePath / "users" / userId.value / "playlists")
        .withQueryParam("limit", limit.value)
        .withQueryParam("offset", offset.value)
    }.runWith(client)
  }

  /**
    * https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-get-playlist
    *
    * @param playlistId The Spotify ID for the playlist.
    *
    * @param market An ISO 3166-1 alpha-2 country code or the string from_token.
    *               Provide this parameter if you want to apply Track Relinking.
    *               For episodes, if a valid user access token is specified in the request header,
    *               the country associated with the user account will take priority over this parameter.
    *               Note: If neither market or user country are provided,
    *               the episode is considered unavailable for the client.
    *
    * TODO: @param additional_types  A comma-separated list of item types that your client supports besides the default track type. Valid types are: track and episode. Note: This parameter was introduced to allow existing clients to maintain their current behaviour and might be deprecated in the future. In addition to providing this parameter, make sure that your client properly handles cases of new types in the future by checking against the type field of each object.
    *
    * @param signer A valid access token from the Spotify Accounts service:
    *               see the Web API Authorization Guide for details.
    *               Both Public and Private playlists belonging to any user
    *               are retrievable on provision of a valid access token.
    *
    * @return On success, the response body contains a playlist object in JSON format
    *         and the HTTP status code in the response header is 200 OK.
    *         If an episode is unavailable in the given market,
    *         its information will not be included in the response.
    *         On error, the header status code is an error code and the response body contains an error object.
    *         Requesting playlists that you do not have the user’s authorization to access returns error 403 Forbidden.
    */
  def getPlaylist(playlistId: SpotifyId, market: Option[Market] = None)(
    implicit signer: SignerV2
  ): F[HttpResponse[FullPlaylist]] =
    new AuthJsonRequest.Get[FullPlaylist] {
      override val uri: Uri = (basePath / "playlists" / playlistId.value)
        .withOptionQueryParam("market", market.map(_.value))
    }.runWith(client)

  /**
    * https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-get-playlist
    *
    * @param playlistId The Spotify ID for the playlist.
    *
    * @param fields Filters for the query: a comma-separated list of the fields to return.
    *                If omitted, all fields are returned.
    *                For example, to get just the playlist’s description and URI: fields=description,uri.
    *                A dot separator can be used to specify non-reoccurring fields,
    *                while parentheses can be used to specify reoccurring fields within objects.
    *                For example, to get just the added date and user ID of the adder:
    *                fields=tracks.items(added_at,added_by.id).
    *                Use multiple parentheses to drill down into nested objects,
    *                for example: fields=tracks.items(track(name,href,album(name,href))).
    *                Fields can be excluded by prefixing them with an exclamation mark,
    *                for example: fields=tracks.items(track(name,href,album(!name,href)))
    *
    * @param market An ISO 3166-1 alpha-2 country code or the string from_token.
    *               Provide this parameter if you want to apply Track Relinking.
    *               For episodes, if a valid user access token is specified in the request header,
    *               the country associated with the user account will take priority over this parameter.
    *               Note: If neither market or user country are provided,
    *               the episode is considered unavailable for the client.
    *
    * TODO: @param additional_types  A comma-separated list of item types that your client supports besides the default track type. Valid types are: track and episode. Note: This parameter was introduced to allow existing clients to maintain their current behaviour and might be deprecated in the future. In addition to providing this parameter, make sure that your client properly handles cases of new types in the future by checking against the type field of each object.
    *
    * @param signer A valid access token from the Spotify Accounts service:
    *               see the Web API Authorization Guide for details.
    *               Both Public and Private playlists belonging to any user
    *               are retrievable on provision of a valid access token.
    *
    * @return On success, the response body contains a playlist object in JSON format
    *         and the HTTP status code in the response header is 200 OK.
    *         If an episode is unavailable in the given market,
    *         its information will not be included in the response.
    *         On error, the header status code is an error code and the response body contains an error object.
    *         Requesting playlists that you do not have the user’s authorization to access returns error 403 Forbidden.
    */
  def getPlaylistFields[PartialPlaylist](playlistId: SpotifyId, fields: String, market: Option[Market] = None)(
    implicit signer: SignerV2,
    partialPlaylistDecoder: Decoder[PartialPlaylist]
  ): F[HttpResponse[PartialPlaylist]] = {
    implicit val pipeDecoder: Pipe[F, Json, PartialPlaylist] = deriveJsonPipe[F, PartialPlaylist]
    new AuthJsonRequest.Get[PartialPlaylist] {
      override val uri: Uri = (basePath / "playlists" / playlistId.value)
        .withQueryParam("fields", fields)
        .withOptionQueryParam("market", market.map(_.value))
    }.runWith(client)
  }
}
