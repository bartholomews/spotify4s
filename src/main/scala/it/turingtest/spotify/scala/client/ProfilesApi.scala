package it.turingtest.spotify.scala.client

import javax.inject.Inject

import it.turingtest.spotify.scala.client.entities.{Page, SimplePlaylist, User, UserPrivate}
import it.turingtest.spotify.scala.client.logging.AccessLogging
import it.turingtest.spotify.scala.client.utils.ConversionUtils
import play.api.libs.ws.WSClient

import scala.concurrent.Future

/**
  * @see https://developer.spotify.com/web-api/user-profile-endpoints/
  */
class ProfilesApi @Inject()(ws: WSClient, api: BaseApi) extends AccessLogging {

  private final val ME = s"${api.BASE_URL}/me"

  /**
    * Get current user's profile
    * @see https://developer.spotify.com/web-api/get-current-users-profile/
    *
    * [OAUTH]
    * Reading the user's email address requires the user-read-email scope;
    * reading country and product subscription level requires the user-read-private scope.
    * Reading the user's birthdate requires the user-read-birthdate scope.
    *
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains a user object in JSON format.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    *         When requesting fields that you don’t have the user’s authorization to access,
    *         it will return error 403 Forbidden.
    */
  def me: Future[UserPrivate] = api.getWithOAuth[UserPrivate](ME)

  /**
    * Get a user's profile
    * @see https://developer.spotify.com/web-api/get-users-profile/
    *
    * @param user_id The user's Spotify user ID
    *
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains a user object in JSON format.
    *         On error, the header status code is an error code
    *         and the response body contains an error object (@see `ErrorDetails`)
    *         If a user with that user_id doesn’t exist, the status code is 404 NOT FOUND.
    */
  def user(user_id: String): Future[User] = api.get[User](s"${api.BASE_URL}/users/$user_id")

  /**
    * Get a list of the current user's playlists
    * @see https://developer.spotify.com/web-api/get-a-list-of-current-users-playlists/
    *
    * Private playlists are only retrievable for the current user
    * and requires the playlist-read-private scope to have been authorized by the user.
    * Note that this scope alone will not return collaborative playlists,
    * even though they are always private.
    *
    * Collaborative playlists are only retrievable for the current user
    * and requires the playlist-read-collaborative scope to have been authorized by the user.
    *
    * @param limit Optional. The maximum number of playlists to return.
    *              Default: 20. Minimum: 1. Maximum: 50.
    *
    * @param offset Optional. The index of the first playlist to return.
    *               Default: 0 (the first object). Maximum offset: 100.000.
    *               Use with limit to get the next set of playlists.
    *
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains an array of simplified playlist objects
    *         (wrapped in a paging object) in JSON format.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    *         Please note that the access token has to be tied to a user.
    */
  def myPlaylists(limit: Option[Int] = None, offset: Option[Int] = None): Future[Page[SimplePlaylist]] = {
    val query = ConversionUtils.seq(("limit", limit), ("offset", offset))
    api.getWithOAuth[Page[SimplePlaylist]](s"$ME/playlists", query: _*)
  }
  def myPlaylists: Future[Page[SimplePlaylist]] = myPlaylists()



}
