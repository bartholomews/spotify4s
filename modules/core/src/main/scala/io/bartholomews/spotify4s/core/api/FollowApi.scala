package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptyList
import eu.timepit.refined.api.Validate.Plain
import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.collection.MaxSize
import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.predicates.all.Size
import eu.timepit.refined.refineV
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.ResponseHandler
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.spotify4s.core.api.FollowApi.{ArtistsFollowingIds, UserIdsFollowingPlaylist, UsersFollowingIds}
import io.bartholomews.spotify4s.core.api.SpotifyApi.apiUri
import io.bartholomews.spotify4s.core.entities.SpotifyId.{SpotifyArtistId, SpotifyPlaylistId, SpotifyUserId}
import io.bartholomews.spotify4s.core.entities.{ArtistsResponse, CursorPage, FullArtist}
import io.bartholomews.spotify4s.core.validators.RefinedValidators.{maxSizeP, NelMaxSizeValidators}
import shapeless.Nat._0
import shapeless.Witness
import sttp.client3.{Response, ResponseException}
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/#category-follow
private[spotify4s] class FollowApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import FollowApi.FollowedArtists
  import eu.timepit.refined.auto.autoRefineV
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  private[api] val basePath: Uri = apiUri / "v1"

  /**
    * Follow a Playlist
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-follow-playlist
    * Add the current user as a follower of a playlist.
    *
    * @param playlistId The Spotify ID of the playlist. Any playlist can be followed,
    *                   regardless of its public/private status, as long as you know its playlist ID.
    * @param signer Required.
    *               A valid access token from the Spotify Accounts service:
    *               see the Web API Authorization Guide for details. The access token must have been issued on behalf of the user.
    *               Following a playlist publicly requires authorization of the playlist-modify-public scope;
    *               following a playlist privately requires the playlist-modify-private scope. See Using Scopes.
    *               Note that the scopes you provide relate only to whether the current user
    *               is following the playlist publicly or privately (i.e. showing others what they are following),
    *               not whether the playlist itself is public or private.
    * @return On success, the HTTP status code in the response header is 200 OK and the response body is empty.
    *         On error, the header status code is an error code and the response body contains an error object.
    */
  def followPlaylist(
    playlistId: SpotifyPlaylistId
  )(signer: SignerV2): F[Response[Either[ResponseException[String, Nothing], Unit]]] = {
    val uri = basePath / "playlists" / playlistId.value / "followers"
    baseRequest(client.userAgent)
      .put(uri)
      .sign(signer)
      .response(asUnit)
      .send(client.backend)
  }

  /**
    * Unfollow Playlist
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-unfollow-playlist
    * Remove the current user as a follower of a playlist.
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
  def unfollowPlaylist(
    playlistId: SpotifyPlaylistId
  )(signer: SignerV2): F[Response[Either[ResponseException[String, Nothing], Unit]]] = {
    val uri = basePath / "playlists" / playlistId.value / "followers"
    baseRequest(client.userAgent)
      .delete(uri)
      .sign(signer)
      .response(asUnit)
      .send(client.backend)
  }

  /**
    * Check if Users Follow a Playlist
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-check-if-user-follows-playlist
    * Check to see if one or more Spotify users are following a specified playlist.
    *
    * @param playlistId The Spotify ID of the playlist.
    * @param userIds A list of Spotify User IDs; the ids of the users that you want to check to see if they follow the playlist. Maximum: 5 ids.
    * @param signer A valid user access token or your client credentials. Requires the playlist-read-private scope if a private playlist is requested.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK and the response body contains a JSON array of true or false values, in the same order in which the ids were specified. On error, the header status code is an error code and the response body contains an error object.
    */
  def usersFollowingPlaylist[DE](playlistId: SpotifyPlaylistId, userIds: UserIdsFollowingPlaylist)(signer: SignerV2)(
    implicit responseHandler: ResponseHandler[DE, List[Boolean]]
  ): F[Response[Either[ResponseException[String, DE], Map[SpotifyUserId, Boolean]]]] = {
    val uri = (basePath / "playlists" / playlistId.value / "followers" / "contains")
      .withQueryParam("ids", userIds.value.map(_.value).toList.mkString(","))

    //noinspection DuplicatedCode
    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(res => userIds.value.toList.zip(res).toMap)
      .send(client.backend)
  }

  /**
    * Get User's Followed Artists
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-followed
    * Get the current user’s followed artists.
    *
    * @param after The last artist ID retrieved from the previous request.
    * @param limit The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50.
    * @param signer A valid user access token. Requires the user-follow-modify scope.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK and the response body contains an artists object.
    *         The artists object in turn contains a cursor-based paging object of Artists.
    *         On error, the header status code is an error code and the response body contains an error object.
    */
  def getFollowedArtists[DE](after: Option[SpotifyArtistId] = None, limit: FollowedArtists.Limit = 20)(signer: SignerV2)(
    implicit responseHandler: ResponseHandler[DE, ArtistsResponse]
  ): F[Response[Either[ResponseException[String, DE], CursorPage[SpotifyArtistId, FullArtist]]]] = {
    val uri = (basePath / "me" / "following")
      .withQueryParam("type", "artist")
      .withQueryParam("limit", limit.value.toString)
      .withOptionQueryParam("after", after.map(_.value))

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(_.artists)
      .send(client.backend)
  }

  /**
    * Follow Artists
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-follow-artists-users
    * Add the current user as a follower of one or more artists.
    *
    * @param ids A comma-separated list of the artist Spotify IDs. For example: ids=74ASZWbe4lXaubB36ztrGX,08td7MxkoHQkXnWAYD8d6Q. A maximum of 50 IDs can be sent in one request.
    * @param signer A valid user access token. Requires the user-follow-modify scope.
    * @return On success, the HTTP status code in the response header is 204 No Content and the response body is empty. On error, the header status code is an error code and the response body contains an error object.
    */
  def followArtists(
    ids: ArtistsFollowingIds
  )(signer: SignerV2): F[Response[Either[ResponseException[String, Nothing], Unit]]] =
    followArtistsOrUsers(entityType = "artist", ids.value.map(_.value))(signer)

  /**
    * Follow Users
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-follow-artists-users
    * Add the current user as a follower of other Spotify users.
    *
    * @param ids A comma-separated list of the user Spotify IDs. For example: ids=74ASZWbe4lXaubB36ztrGX,08td7MxkoHQkXnWAYD8d6Q. A maximum of 50 IDs can be sent in one request.
    * @param signer A valid user access token. Requires the user-follow-modify scope.
    * @return On success, the HTTP status code in the response header is 204 No Content and the response body is empty. On error, the header status code is an error code and the response body contains an error object.
    */
  def followUsers(
    ids: UsersFollowingIds
  )(signer: SignerV2): F[Response[Either[ResponseException[String, Nothing], Unit]]] =
    followArtistsOrUsers(entityType = "user", ids.value.map(_.value))(signer)

  private def followArtistsOrUsers(entityType: String, ids: NonEmptyList[String])(signer: SignerV2) = {
    //noinspection DuplicatedCode
    val uri = (basePath / "me" / "following")
      .withQueryParam("type", entityType)
      .withQueryParam("ids", ids.toList.mkString(","))

    baseRequest(client.userAgent)
      .put(uri)
      .sign(signer)
      .response(asUnit)
      .send(client.backend)
  }

  /**
    * Unfollow Artists
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-unfollow-artists-users
    * Remove the current user as a follower of one or more artists.
    *
    * @param ids A comma-separated list of the artist Spotify IDs. For example: ids=74ASZWbe4lXaubB36ztrGX,08td7MxkoHQkXnWAYD8d6Q. A maximum of 50 IDs can be sent in one request.
    * @param signer A valid user access token. Requires the user-follow-modify scope.
    * @return On success, the HTTP status code in the response header is 204 No Content and the response body is empty. On error, the header status code is an error code and the response body contains an error object.
    */
  def unfollowArtists(
    ids: ArtistsFollowingIds
  )(signer: SignerV2): F[Response[Either[ResponseException[String, Nothing], Unit]]] =
    unfollowArtistsOrUsers(entityType = "artist", ids.value.map(_.value))(signer)

  /**
    * Unfollow Users
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-unfollow-artists-users
    * Remove the current user as a follower of other Spotify users.
    *
    * @param ids A comma-separated list of the user Spotify IDs. For example: ids=74ASZWbe4lXaubB36ztrGX,08td7MxkoHQkXnWAYD8d6Q. A maximum of 50 IDs can be sent in one request.
    * @param signer A valid user access token. Requires the user-follow-modify scope.
    * @return On success, the HTTP status code in the response header is 204 No Content and the response body is empty. On error, the header status code is an error code and the response body contains an error object.
    */
  def unfollowUsers(
    ids: UsersFollowingIds
  )(signer: SignerV2): F[Response[Either[ResponseException[String, Nothing], Unit]]] =
    unfollowArtistsOrUsers(entityType = "user", ids.value.map(_.value))(signer)

  private def unfollowArtistsOrUsers(entityType: String, ids: NonEmptyList[String])(signer: SignerV2) = {
    //noinspection DuplicatedCode
    val uri = (basePath / "me" / "following")
      .withQueryParam("type", entityType)
      .withQueryParam("ids", ids.toList.mkString(","))

    baseRequest(client.userAgent)
      .delete(uri)
      .sign(signer)
      .response(asUnit)
      .send(client.backend)
  }

  /**
    * Get Following State for Artists
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-check-current-user-follows
    * Check to see if the current user is following one or more artists.
    *
    * @param ids A comma-separated list of the artist Spotify IDs to check. For example: ids=74ASZWbe4lXaubB36ztrGX,08td7MxkoHQkXnWAYD8d6Q. A maximum of 50 IDs can be sent in one request.
    * @param signer A valid user access token. Requires the user-follow-read scope.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK and the response body contains a JSON array of true or false values, in the same order in which the ids were specified. On error, the header status code is an error code and the response body contains an error object.
    */
  def isFollowingArtists[DE](ids: ArtistsFollowingIds)(signer: SignerV2)(
    implicit responseHandler: ResponseHandler[DE, List[Boolean]]
  ): F[Response[Either[ResponseException[String, DE], Map[SpotifyArtistId, Boolean]]]] = {
    val uri = (basePath / "me" / "following" / "contains")
      .withQueryParam("type", "artist")
      .withQueryParam("ids", ids.value.toList.map(_.value).mkString(","))

    //noinspection DuplicatedCode
    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(res => ids.value.toList.zip(res).toMap)
      .send(client.backend)
  }

  /**
    * Get Following State for Users
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-check-current-user-follows
    * Check to see if the current user is following other Spotify users.
    *
    * @param ids A comma-separated list of the user Spotify IDs to check. For example: ids=74ASZWbe4lXaubB36ztrGX,08td7MxkoHQkXnWAYD8d6Q. A maximum of 50 IDs can be sent in one request.
    * @param signer A valid user access token. Requires the user-follow-read scope.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK and the response body contains a JSON array of true or false values, in the same order in which the ids were specified. On error, the header status code is an error code and the response body contains an error object.
    */
  def isFollowingUsers[DE](ids: UsersFollowingIds)(signer: SignerV2)(
    implicit responseHandler: ResponseHandler[DE, List[Boolean]]
  ): F[Response[Either[ResponseException[String, DE], Map[SpotifyUserId, Boolean]]]] = {
    val uri = (basePath / "me" / "following" / "contains")
      .withQueryParam("type", "user")
      .withQueryParam("ids", ids.value.toList.map(_.value).mkString(","))

    //noinspection DuplicatedCode
    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(res => ids.value.toList.zip(res).toMap)
      .send(client.backend)
  }
}

object FollowApi {
  type UserIdsFollowingPlaylist = Refined[NonEmptyList[SpotifyUserId], MaxSize[5]]
  type ArtistsFollowingIds = Refined[NonEmptyList[SpotifyArtistId], MaxSize[50]]
  type UsersFollowingIds = Refined[NonEmptyList[SpotifyUserId], MaxSize[50]]

  object FollowedArtists {
    type Limit = Int Refined Interval.Closed[1, 50]
  }

  object UserIdsFollowingPlaylist extends NelMaxSizeValidators[SpotifyUserId, UserIdsFollowingPlaylist](maxSize = 5) {
    private def validateUserIdsFollowingPlaylist: Plain[NonEmptyList[SpotifyUserId], MaxSize[5]] = {
      Validate
        .fromPredicate(
          (d: NonEmptyList[SpotifyUserId]) => d.length <= 5,
          (_: NonEmptyList[SpotifyUserId]) => "a maximum of 5 ids can be set in one request",
          Size[Interval.Closed[_0, Witness.`5`.T]](maxSizeP)
        )
    }

    override def fromNel(xs: NonEmptyList[SpotifyUserId]): Either[String, UserIdsFollowingPlaylist] =
      refineV[MaxSize[5]](xs)(validateUserIdsFollowingPlaylist)
  }

  object ArtistsFollowingIds extends NelMaxSizeValidators[SpotifyArtistId, ArtistsFollowingIds](maxSize = 50) {
    private def validateArtistsFollowingIds: Plain[NonEmptyList[SpotifyArtistId], MaxSize[50]] = {
      Validate
        .fromPredicate(
          (d: NonEmptyList[SpotifyArtistId]) => d.length <= 50,
          (_: NonEmptyList[SpotifyArtistId]) => "a maximum of 50 ids can be set in one request",
          Size[Interval.Closed[_0, Witness.`50`.T]](maxSizeP)
        )
    }

    override def fromNel(xs: NonEmptyList[SpotifyArtistId]): Either[String, ArtistsFollowingIds] =
      refineV[MaxSize[50]](xs)(validateArtistsFollowingIds)
  }

  object UsersFollowingIds extends NelMaxSizeValidators[SpotifyUserId, UsersFollowingIds](maxSize = 50) {
    private def validateUsersFollowingIds: Plain[NonEmptyList[SpotifyUserId], MaxSize[50]] = {
      Validate
        .fromPredicate(
          (d: NonEmptyList[SpotifyUserId]) => d.length <= 50,
          (_: NonEmptyList[SpotifyUserId]) => "a maximum of 50 ids can be set in one request",
          Size[Interval.Closed[_0, Witness.`50`.T]](maxSizeP)
        )
    }

    override def fromNel(xs: NonEmptyList[SpotifyUserId]): Either[String, UsersFollowingIds] =
      refineV[MaxSize[50]](xs)(validateUsersFollowingIds)
  }
}
