package it.turingtest.spotify.scala.client

import javax.inject.Inject

import com.vitorsvieira.iso.ISOCountry.ISOCountry
import it.turingtest.spotify.scala.client.entities.{FullPlaylist, Page, PlaylistTrack, SimplePlaylist}
import it.turingtest.spotify.scala.client.logging.AccessLogging
import it.turingtest.spotify.scala.client.utils.ConversionUtils
import play.api.libs.ws.WSClient

import scala.concurrent.Future

/**
  * @see https://developer.spotify.com/web-api/playlist-endpoints/
  */
class PlaylistsApi @Inject()(ws: WSClient, api: BaseApi, profiles: ProfilesApi) extends AccessLogging {

  // ===================================================================================================================

  /**
    * Get a playlist
    *
    * @see https://developer.spotify.com/web-api/get-playlist/
    *
    * @param user_id The user's Spotify user ID.
    *
    * @param playlist_id The Spotify ID for the playlist.
    *
    * @param market Optional. An ISO 3166-1 alpha-2 country code.
    *               Provide this parameter if you want to apply Track Relinking.
    *
    * @return On success, the response body contains a playlist object in JSON format
    *         and the HTTP status code in the response header is 200 OK.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    *         Requesting playlists that you do not have the user’s authorization to access returns error 403 Forbidden.
    */
  def playlist(user_id: String, playlist_id: String, market: Option[ISOCountry] = None): Future[FullPlaylist] = {

    val query: Seq[(String, String)] = ConversionUtils.seq(("market", market))
    api.get[FullPlaylist](s"${api.BASE_URL}/users/$user_id/playlists/$playlist_id", query: _*)
  }

  /**
    * Get a list of a user's playlists
    *
    * @see https://developer.spotify.com/web-api/get-list-users-playlists/
    *
    * @param user_id The user's Spotify user ID.
    *
    * @param limit Optional. The maximum number of playlists to return.
    *              Default: 20. Minimum: 1. Maximum: 50.
    *
    * @param offset Optional. The index of the first playlist to return.
    *               Default: 0 (the first object). Maximum offset: 100.000.
    *               Use with limit to get the next set of playlists.
    *
    *  Playlists are only retrievable for the current user
    *  and requires the `playlist-read-private scope` to have been authorized by the user.
    *  Note that this scope alone will not return collaborative playlists,
    *  even though they are always private.
    *
    *  Collaborative playlists are only retrievable for the current user
    *  and requires the `playlist-read-collaborative` scope to have been authorized by the user.
    *
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains an array of simplified playlist objects
    *         (wrapped in a paging object) in JSON format.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    */
  def playlists(user_id: String, limit: Option[Int] = None,
                offset: Option[Int] = None): Future[Page[SimplePlaylist]] = {

    val query: Seq[(String, String)] = ConversionUtils.seq(("limit", limit), ("offset", offset))
    api.getWithOAuth[Page[SimplePlaylist]](s"${api.BASE_URL}/users/$user_id/playlists", query: _*)
  }

  /**
    * Get a list of the current user's playlists
    * @see https://developer.spotify.com/web-api/get-a-list-of-current-users-playlists/
    * @return
    */
  def myPlaylists: Future[Page[SimplePlaylist]] = {
    api.getWithOAuth[Page[SimplePlaylist]](s"${api.BASE_URL}/me/playlists")
  }

  /**
    * TODO
    * Follow a playlist
    * @see https://developer.spotify.com/web-api/follow-playlist/
    * @return
    */
  def follow() = ???

  /**
    * TODO
    * Unfollow a playlist
    * @see https://developer.spotify.com/web-api/unfollow-playlist/
    * @return
    */
  def unfollow() = ???

  /**
    * TODO
    * Search for a playlist
    * @see https://developer.spotify.com/web-api/search-item/
    * @return
    */
  def search() = ???

  /**
    * TODO
    * Create a playlist
    * @see https://developer.spotify.com/web-api/create-playlist/
    * @return
    */
  def create() = ???

  /**
    * Change a playlist's details
    * @see https://developer.spotify.com/web-api/change-playlist-details/
    */
  def changeDetails() = ???

  /**
    * Check if Users follow a playlist
    * @see https://developer.spotify.com/web-api/check-user-following-playlist/
    */
  def isFollowing: Future[Boolean] = ???

  /**
    * Upload a Custom playlist cover image
    * @see https://developer.spotify.com/web-api/upload-a-custom-playlist-cover-image/
    */
  def uploadCoverImage() = ???

  // ===================================================================================================================
  // PLAYLIST TRACKS

  /**
    * Get a playlist's tracks
    * @see https://developer.spotify.com/web-api/get-playlists-tracks/
    * @param user_id The user's Spotify user ID.
    * @param playlist_id The Spotify ID for the playlist.
    * @return On success, the response body contains an array of playlist track objects
    *         (wrapped in a paging object) in JSON format
    *         and the HTTP status code in the response header is 200 OK.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    *         Requesting playlists that you do not have the user’s authorization to access returns error 403 Forbidden.
    */
  def tracks(user_id: String, playlist_id: String): Future[Page[PlaylistTrack]] = {
    api.getWithOAuth[Page[PlaylistTrack]](s"${api.BASE_URL}/users/$user_id/playlists/$playlist_id/tracks")
  }

  /**
    * Add tracks to a playlist
    * @see https://developer.spotify.com/web-api/add-tracks-to-playlist/
    */
  def addTracks() = ???

  /**
    * Remove tracks from a playlist
    * @see https://developer.spotify.com/web-api/remove-tracks-playlist/
    */
  def removeTracks() = ???

  /**
    * Reorder a playlist's tracks
    * @see https://developer.spotify.com/web-api/reorder-playlists-tracks/
    */
  def reorderTracks() = ???

  /**
    * Replace a playlist's tracks
    * @see https://developer.spotify.com/web-api/replace-playlists-tracks/
    */
  def replaceTracks() = ???

  // ===================================================================================================================


}
