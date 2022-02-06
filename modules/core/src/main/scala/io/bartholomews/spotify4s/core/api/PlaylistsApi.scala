package io.bartholomews.spotify4s.core.api

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.GreaterEqual
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.{ResponseHandler, SttpResponse}
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.api.BrowseApi.Limit
import io.bartholomews.spotify4s.core.api.PlaylistsApi.TracksPosition
import io.bartholomews.spotify4s.core.api.SpotifyApi.{basePath, Offset, SpotifyUris}
import io.bartholomews.spotify4s.core.entities.SpotifyId.SpotifyUserId
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.core.entities.requests.{
  AddTracksToPlaylistRequest,
  CreatePlaylistRequest,
  ModifyPlaylistRequest
}
import sttp.client3.BodySerializer
import sttp.model.Uri

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private[spotify4s] class PlaylistsApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import eu.timepit.refined.auto.autoRefineV
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  private[api] val browsePath: Uri = basePath / "browse"

  /**
    * Get Playlist
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-playlist
    *
    * Get a playlist owned by a Spotify user.
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
  def getPlaylist[DE](playlistId: SpotifyId, market: Option[Market] = None)(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, FullPlaylist]): F[SttpResponse[DE, FullPlaylist]] = {
    val uri: Uri = (basePath / "playlists" / playlistId.value)
      .withOptionQueryParam("market", market.map(_.value))

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }

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
  def getPlaylistFields[DE, PartialPlaylist](playlistId: SpotifyId, fields: String, market: Option[Market] = None)(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, PartialPlaylist]): F[SttpResponse[DE, PartialPlaylist]] = {
    val uri: Uri = (basePath / "playlists" / playlistId.value)
      .withQueryParam("fields", fields)
      .withOptionQueryParam("market", market.map(_.value))

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }

  /**
    * Change Playlist Details
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/change-playlist-details
    *
    * Change a playlist's name and public/private state. (The user must, of course, own the playlist.)
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
  )(
    signer: SignerV2
  )(implicit bodySerializer: BodySerializer[ModifyPlaylistRequest]): F[SttpResponse[Nothing, Unit]] = {
    val uri: Uri = basePath / "playlists" / playlistId.value
    baseRequest(client.userAgent)
      .put(uri)
      .body(ModifyPlaylistRequest(playlistName, public, collaborative, description))
      .sign(signer)
      .response(asUnit)
      .send(client.backend)
  }

  /**
    * Get Playlist Items
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-playlists-tracks
    *
    * Get full details of the items of a playlist owned by a Spotify user.
    *
    * @param playlistId The Spotify ID for the playlist.
    * @param market Optional. An ISO 3166-1 alpha-2 country code or the string from_token. Provide this parameter if you want to apply Track Relinking. For episodes, if a valid user access token is specified in the request header, the country associated with the user account will take priority over this parameter.
    *               _Note: If neither market or user country are provided, the episode is considered unavailable for the client.
    * @param limit Optional. The maximum number of items to return. Default: 100. Minimum: 1. Maximum: 100.
    * @param offset Optional. The index of the first item to return. Default: 0 (the first object).
    * @param signer Required. A valid access token from the Spotify Accounts service: see the Web API Authorization Guide for details. Both Public and Private playlists belonging to any user are retrievable on provision of a valid access token.
    * @return On success, the response body contains an array of track objects and episode objects (depends on the additional_types parameter), wrapped in a paging object in JSON format and the HTTP status code in the response header is 200 OK. If an episode is unavailable in the given market, its information will not be included in the response. On error, the header status code is an error code and the response body contains an error object. Requesting playlists that you do not have the user’s authorization to access returns error 403 Forbidden.
    */
  def getPlaylistItems[DE](
    playlistId: SpotifyId,
    market: Market,
    limit: SimplePlaylistItem.Limit = 100,
    offset: Offset = 0
  )(signer: SignerV2)(
    implicit responseHandler: ResponseHandler[DE, Page[SimplePlaylistItem]]
  ): F[SttpResponse[DE, Page[SimplePlaylistItem]]] = {
    val uri: Uri = (basePath / "playlists" / playlistId.value / "tracks")
      .withQueryParam("market", market.value)
      .withQueryParam("limit", limit.value.toString)
      .withQueryParam("offset", offset.toString)

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }

  /**
    * Add Items to Playlist
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/add-tracks-to-playlist
    *
    * Add one or more items to a user's playlist.
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
  def addItemsToPlaylist[DE](
    playlistId: SpotifyId,
    uris: SpotifyUris,
    position: Option[TracksPosition]
  )(signer: SignerV2)(
    implicit bodySerializer: BodySerializer[AddTracksToPlaylistRequest],
    responseHandler: ResponseHandler[DE, SnapshotIdResponse]
  ): F[SttpResponse[DE, SnapshotId]] = {
    val uri: Uri = basePath / "playlists" / playlistId.value / "tracks"

    baseRequest(client.userAgent)
      .post(uri)
      .body(AddTracksToPlaylistRequest(uris.value.toList.map(_.value), position.map(_.value)))
      .sign(signer)
      .response(responseHandler)
      .mapResponse(_.map(_.snapshotId))
      .send(client.backend)
  }

  /**
    *
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/reorder-or-replace-playlists-tracks
    *
    * Either reorder or replace items in a playlist depending on the request's parameters. T
    * TODO - Currently not supported - To reorder items, include `range_start`, `insert_before`, `range_length` and `snapshot_id` in the request's body.
    * To replace items, include `uri`s as either a query parameter or in the request's body.
    * Replacing items in a playlist will overwrite its existing items.
    * This operation can be used for replacing or clearing items in a playlist.
    * Note: Replace and reorder are mutually exclusive operations which share the same endpoint,
    * but have different parameters. These operations can't be applied together in a single request.
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
  def updatePlaylistItems(playlistId: SpotifyId, uris: SpotifyUris)(
    signer: SignerV2
  ): F[SttpResponse[Nothing, Unit]] = {
    val uri: Uri = (basePath / "users" / "playlists")
      .withQueryParam(key = "uris", uris.value.toList.map(_.value).mkString(","))

    baseRequest(client.userAgent)
      .put(uri)
      .sign(signer)
      .response(asUnit)
      .send(client.backend)
  }

  // TODO - Remove Playlist Items
  // https://developer.spotify.com/documentation/web-api/reference/#/operations/remove-tracks-playlist

  /**
    * Get Current User's Playlists
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-a-list-of-current-users-playlists
    *
    * Get a list of the playlists owned or followed by the current Spotify user.
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
  def getMyPlaylists[DE](limit: SimplePlaylist.Limit = 20, offset: Offset = 0)(signer: SignerV2)(
    implicit responseHandler: ResponseHandler[DE, Page[SimplePlaylist]]
  ): F[SttpResponse[DE, Page[SimplePlaylist]]] = {
    val uri: Uri = (basePath / "me" / "playlists")
      .withQueryParam("limit", limit.value.toString)
      .withQueryParam("offset", offset.toString)

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }

  /**
    * Get User's Playlists
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-list-users-playlists
    *
    * Get a list of the playlists owned or followed by a Spotify user.
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
  def getUserPlaylists[DE](userId: SpotifyUserId, limit: SimplePlaylist.Limit = 20, offset: Offset = 0)(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, Page[SimplePlaylist]]): F[SttpResponse[DE, Page[SimplePlaylist]]] = {
    val uri: Uri = (basePath / "users" / userId.value / "playlists")
      .withQueryParam("limit", limit.value.toString)
      .withQueryParam("offset", offset.toString)

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }

  /**
    * Create Playlist
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/create-playlist
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
  def createPlaylist[DE](
    userId: SpotifyUserId,
    playlistName: String,
    public: Boolean = true,
    collaborative: Boolean = false,
    description: Option[String] = None
  )(signer: SignerV2)(
    implicit bodySerializer: BodySerializer[CreatePlaylistRequest],
    responseHandler: ResponseHandler[DE, FullPlaylist]
  ): F[SttpResponse[DE, FullPlaylist]] = {
    val uri: Uri = basePath / "users" / userId.value / "playlists"

    baseRequest(client.userAgent)
      .post(uri)
      .body(CreatePlaylistRequest(playlistName, public, collaborative, description))
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }

  /**
    * Get Featured Playlists
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-featured-playlists
    *
    * Get a list of Spotify featured playlists (shown, for example, on a Spotify player’s ‘Browse’ tab).
    *
    * @param country Optional. A country: an ISO 3166-1 alpha-2 country code.
    *                (https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2)
    *                Provide this parameter if you want the list of returned items
    *                to be relevant to a particular country.
    *                If omitted, the returned items will be relevant to all countries.
    * @param locale Optional. The desired language, consisting of a lowercase ISO 639-1 language code
    *               and an uppercase ISO 3166-1 alpha-2 country code, joined by an underscore.
    *               For example: es_MX, meaning “Spanish (Mexico)”.
    *               Provide this parameter if you want the results returned in a particular language (where available).
    *               Note that, if locale is not supplied, or if the specified language is not available,
    *               all strings will be returned in the Spotify default language (American English).
    *               The locale parameter, combined with the country parameter,
    *               may give odd results if not carefully matched.
    *               For example country=SE&locale=de_DE will return a list of categories relevant to Sweden
    *               but as German language strings.
    * @param timestamp Optional. A timestamp in ISO 8601 format: yyyy-MM-ddTHH:mm:ss.
    *                  Use this parameter to specify the user’s local time
    *                  to get results tailored for that specific date and time in the day.
    *                  If not provided, the response defaults to the current UTC time.
    *                  Example: “2014-10-23T09:00:00” for a user whose local time is 9AM.
    *                  If there were no featured playlists (or there is no data) at the specified time,
    *                  the response will revert to the current UTC time.
    * @param limit Optional. The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50.
    * @param offset Optional. The index of the first item to return. Default: 0 (the first object).
    *               Use with limit to get the next set of items.
    * @param signer A valid user access token or your client credentials.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *          and the response body contains a message and a playlists object.
    *          The playlists object contains an array of simplified playlist objects
    *          (wrapped in a paging object) in JSON format.
    *          On error, the header status code is an error code and the response body contains an error object.
    *          Once you have retrieved the list of playlist objects,
    *          you can use Get a Playlist and Get a Playlist’s Tracks to drill down further.
    */
  def getFeaturedPlaylists[DE](
    country: Option[CountryCodeAlpha2],
    locale: Option[Locale],
    timestamp: Option[LocalDateTime],
    limit: Limit = 20,
    offset: Offset = 0
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, FeaturedPlaylists]): F[SttpResponse[DE, FeaturedPlaylists]] = {
    val uri: Uri = (browsePath / "featured-playlists")
      .withOptionQueryParam("country", country.map(_.value))
      .withOptionQueryParam("locale", locale.map(_.value))
      .withOptionQueryParam("timestamp", timestamp.map(_.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
      .withQueryParam("limit", limit.value.toString)
      .withQueryParam("offset", offset.value.toString)

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }

  /**
    * Get Category's Playlists
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-a-categories-playlists
    *
    * Get a list of Spotify playlists tagged with a particular category.
    *
    * @param categoryId Required. The Spotify category ID for the category.
    * @param country Optional. A country: an ISO 3166-1 alpha-2 country code.
    *                Provide this parameter to ensure that the category exists for a particular country.
    * @param limit Optional. The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50.
    * @param offset Optional. The index of the first item to return. Default: 0 (the first object).
    *               Use with limit to get the next set of items.
    * @param signer A valid user access token or your client credentials.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains an array of simplified playlist objects
    *         (wrapped in a paging object) in JSON format.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    *         Once you have retrieved the list, you can use `getPlaylist` and `getPlaylistItems` to drill down further.
    */
  def getCategoryPlaylists[DE](
    categoryId: SpotifyCategoryId,
    country: Option[CountryCodeAlpha2],
    limit: Limit = 20,
    offset: Offset = 0
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, PlaylistsResponse]): F[SttpResponse[DE, Page[SimplePlaylist]]] = {
    val uri: Uri = (browsePath / "categories" / categoryId.value / "playlists")
      .withOptionQueryParam("country", country.map(_.value))
      .withQueryParam("limit", limit.value.toString)
      .withQueryParam("offset", offset.value.toString)

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(_.playlists)
      .send(client.backend)
  }

//  : F[HttpResponse[Unit]] =
//    new FsAuthPlainText.PutEmpty[Unit] {
//      override val uri: Uri = (basePath / "users" / "playlists")
//        .withQueryParam(key = "uris", uris.value.toList.map(_.value).mkString(","))
//    }.runWith(client)

  // https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-get-playlist-cover
  // TODO

  // https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-remove-tracks-playlist
  // TODO
}

object PlaylistsApi {
  type TracksPosition = Refined[Int, GreaterEqual[0]]
}
