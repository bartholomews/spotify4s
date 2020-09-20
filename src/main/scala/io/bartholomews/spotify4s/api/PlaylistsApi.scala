package io.bartholomews.spotify4s.api

import cats.effect.ConcurrentEffect
import io.bartholomews.fsclient.client.FsClient
import io.bartholomews.fsclient.entities.oauth.{Signer, SignerV2}
import io.bartholomews.fsclient.requests.{FsAuth, FsAuthJson}
import io.bartholomews.fsclient.utils.HttpTypes.HttpResponse
import io.bartholomews.spotify4s.api.SpotifyApi.{apiUri, Limit, Offset, SpotifyUris, TracksPosition}
import io.bartholomews.spotify4s.entities.{
  AddTracksToPlaylistRequest,
  CreatePlaylistRequest,
  FullPlaylist,
  Market,
  ModifyPlaylistRequest,
  Page,
  SimplePlaylist,
  SnapshotId,
  SpotifyId,
  SpotifyUserId
}
import io.circe.Decoder
import org.http4s.Uri

// https://developer.spotify.com/console/playlists/
class PlaylistsApi[F[_]: ConcurrentEffect, S <: Signer](client: FsClient[F, S]) {
  import Page.pipeDecoder
  import eu.timepit.refined.auto.autoRefineV
  import io.bartholomews.fsclient.implicits._

  private[api] val basePath: Uri = apiUri / "v1"

  /**
    * https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-replace-playlists-tracks
    *
    * @param playlistId The Spotify ID for the playlist.
    *
    * @param uris A list of Spotify URIs to set, can be track or episode URIs.
    *             For example:
    *             uris=spotify:track:4iV5W9uYEdYUVa79Axb7Rh,spotify:episode:512ojhOuo1ktJprKbVcKyQ
    *             A maximum of 100 items can be set in one request.
    *
    * @param signer A valid access token from the Spotify Accounts service:
    *               see the Web API Authorization Guide for details.
    *               The access token must have been issued on behalf of the user.
    *               Setting items in the current user’s public playlists
    *               requires authorization of the playlist-modify-public scope;
    *               setting items in the current user’s private playlist
    *               (including collaborative playlists) requires the playlist-modify-private scope.
    *
    * @return On success, the HTTP status code in the response header is 201 Created.
    *         On error, the header status code is an error code,
    *         the response body contains an error object,
    *         and the existing playlist is unmodified.
    *         Trying to set an item when you do not have the user’s authorization
    *         returns error 403 Forbidden.
    */
  def replacePlaylistItems(playlistId: SpotifyId, uris: SpotifyUris)(
    implicit signer: SignerV2
  ): F[HttpResponse[Unit]] =
    new FsAuth.PutEmpty {
      override val uri: Uri = (basePath / "users" / "playlists")
        .withQueryParam(key = "uris", uris.value.toList.map(_.value).mkString(","))
    }.runWith(client)

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
  ): F[HttpResponse[Page[SimplePlaylist]]] =
    new FsAuthJson.Get[Page[SimplePlaylist]] {
      override val uri: Uri = (basePath / "users" / userId.value / "playlists")
        .withQueryParam("limit", limit.value)
        .withQueryParam("offset", offset.value)
    }.runWith(client)

  /**
    * https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-change-playlist-details
    *
    * @param playlistId   The Spotify ID for the playlist.
    *
    * @param playlistName The new name for the playlist, for example "My New Playlist Title".
    *
    * @param public If true the playlist will be public, if false it will be private.
    *
    * @param collaborative If true, the playlist will become collaborative and other users
    *                      will be able to modify the playlist in their Spotify client.
    *                      Note: You can only set collaborative to true on non-public playlists.
    *
    * @param description Value for playlist description
    *                    as displayed in Spotify Clients and in the Web API.
    *
    * @param signer A valid access token from the Spotify Accounts service:
    *               see the Web API Authorization Guide for details.
    *               The access token must have been issued on behalf of the user.
    *               Changing a public playlist for a user
    *               requires authorization of the playlist-modify-public scope;
    *               changing a private playlist requires the playlist-modify-private scope.
    *
    * @return On success the HTTP status code in the response header is 200 OK.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    *         Trying to change a playlist when you do not have the user’s authorization
    *         returns error 403 Forbidden.
    */
  def changePlaylistDetails(
    playlistId: SpotifyId,
    playlistName: Option[String],
    public: Option[Boolean],
    collaborative: Option[Boolean],
    description: Option[String]
  )(implicit signer: SignerV2): F[HttpResponse[Unit]] =
    new FsAuth.Put[ModifyPlaylistRequest] {
      override val uri: Uri = basePath / "playlists" / playlistId.value
      override def entityBody: ModifyPlaylistRequest =
        ModifyPlaylistRequest(playlistName, public, collaborative, description)
    }.runWith(client)

  // https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-upload-custom-playlist-cover
  // TODO

  // https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-reorder-playlists-tracks
  // TODO

  /**
    * Add one or more items to a user’s playlist.
    * https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-add-tracks-to-playlist
    *
    * @param playlistId The Spotify ID for the playlist.
    *
    * @param uris     A list of Spotify URIs to add, can be track or episode URIs. For example:
    *                 uris=spotify:track:4iV5W9uYEdYUVa79Axb7Rh,
    *                 spotify:track:1301WleyT98MSxVHPZCA6M,
    *                 spotify:episode:512ojhOuo1ktJprKbVcKyQ
    *                 A maximum of 100 items can be added in one request.
    *
    * @param position The position to insert the items, a zero-based index.
    *                 For example, to insert the items in the first position: position=0;
    *                 to insert the items in the third position: position=2.
    *                 If omitted, the items will be appended to the playlist.
    *                 Items are added in the order they are listed in the query string or request body.
    *
    * @param signer   A valid access token from the Spotify Accounts service:
    *                 see the Web API Authorization Guide for details.
    *                 The access token must have been issued on behalf of the user.
    *                 Adding items to the current user’s public playlists
    *                 requires authorization of the playlist-modify-public scope;
    *                 adding items to the current user’s private playlist
    *                 (including collaborative playlists) requires the playlist-modify-private scope.
    *
    * @return On success, the HTTP status code in the response header is 201 Created.
    *         The response body contains a snapshot_id in JSON format.
    *         The snapshot_id can be used to identify your playlist version in future requests.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    *         Trying to add an item when you do not have the user’s authorization,
    *         or when there are more than 10.000 items in the playlist, returns error 403 Forbidden.
    */
  def addTracksToPlaylist(
    playlistId: SpotifyId,
    uris: SpotifyUris,
    position: Option[TracksPosition]
  )(implicit signer: SignerV2): F[HttpResponse[SnapshotId]] = {
    new FsAuthJson.Post[AddTracksToPlaylistRequest, SnapshotId] {
      override val uri: Uri = basePath / "playlists" / playlistId.value
      override def entityBody: AddTracksToPlaylistRequest =
        AddTracksToPlaylistRequest(uris.value.toList.map(_.value), position.map(_.value))
    }.runWith(client)
  }

  // https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-get-playlists-tracks
  // TODO

  // https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-get-playlist-cover
  // TODO

  // https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-remove-tracks-playlist
  // TODO

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
    new FsAuthJson.Get[FullPlaylist] {
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
  ): F[HttpResponse[PartialPlaylist]] =
    new FsAuthJson.Get[PartialPlaylist] {
      override val uri: Uri = (basePath / "playlists" / playlistId.value)
        .withQueryParam("fields", fields)
        .withOptionQueryParam("market", market.map(_.value))
    }.runWith(client)

  /**
    * https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-create-playlist
    *
    * Create a playlist for a Spotify user. (The playlist will be empty until you add tracks.)
    * Creating a public playlist for a user requires authorization of the playlist-modify-public scope;
    * creating a private playlist requires the playlist-modify-private scope.
    *
    * @param userId The user’s Spotify user ID.
    * @param playlistName The name for the new playlist, for example "Your Coolest Playlist".
    *                     This name does not need to be unique;
    *                     a user may have several playlists with the same name.
    *
    * @param public Defaults to true.
    *               If true the playlist will be public, if false it will be private.
    *               To be able to create private playlists,
    *               the user must have granted the playlist-modify-private scope.
    *
    * @param collaborative Defaults to false. If true the playlist will be collaborative.
    *                      Note that to create a collaborative playlist
    *                      you must also set public to false.
    *                      To create collaborative playlists
    *                      you must have granted playlist-modify-private
    *                      and playlist-modify-public scopes.
    *
    * @param description value for playlist description as displayed
    *                    in Spotify Clients and in the Web API.
    *
    * @param signer A valid access token from the Spotify Accounts service:
    *               see the Web API Authorization Guide for details.
    *               The access token must have been issued on behalf of the user.
    *               Creating a public playlist for a user requires authorization
    *               of the playlist-modify-public scope;
    *               creating a private playlist requires the playlist-modify-private scope.
    *
    * @return On success, the response body contains the created playlist object in JSON format
    *         and the HTTP status code in the response header is 200 OK or 201 Created.
    *         There is also a Location response header
    *         giving the Web API endpoint for the new playlist.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    *         Trying to create a playlist when you do not have the user’s authorization
    *         returns error 403 Forbidden.
    */
  def createPlaylist(
    userId: SpotifyUserId,
    playlistName: String,
    public: Boolean = true,
    collaborative: Boolean = false,
    description: Option[String] = None
  )(
    implicit signer: SignerV2
  ): F[HttpResponse[FullPlaylist]] =
    new FsAuthJson.Post[CreatePlaylistRequest, FullPlaylist] {
      override val uri: Uri = basePath / "users" / userId.value / "playlists"
      override def entityBody: CreatePlaylistRequest =
        CreatePlaylistRequest(playlistName, public, collaborative, description)
    }.runWith(client)
}
