package it.turingtest.spotify.scala.client

import javax.inject.Inject

import it.turingtest.spotify.scala.client.entities.{FullPlaylist, Page, PlaylistTrack, SimplePlaylist}
import it.turingtest.spotify.scala.client.logging.AccessLogging
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @see https://developer.spotify.com/web-api/playlist-endpoints/
  */
class PlaylistsApi @Inject()(configuration: play.api.Configuration, ws: WSClient,
                             api: BaseApi, profiles: ProfilesApi) extends AccessLogging {

  // ===================================================================================================================

  /**
    * @see https://developer.spotify.com/web-api/get-playlist/
    * @param user_id The user's Spotify user ID.
    * @param playlist_id The Spotify ID for the playlist.
    * @return On success, the response body contains a playlist object in JSON format
    *         and the HTTP status code in the response header is 200 OK.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    *         Requesting playlists that you do not have the user’s authorization to access returns error 403 Forbidden.
    */
  def playlist(user_id: String, playlist_id: String): Future[FullPlaylist] = {
    api.getWithOAuth[FullPlaylist](s"${api.BASE_URL}/users/$user_id/playlists/$playlist_id")
  }

  def playlists(user_id: String): Future[Page[SimplePlaylist]] = {
    api.getWithOAuth[Page[SimplePlaylist]](s"${api.BASE_URL}/users/$user_id/playlists")
  }

  /**
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

  def myPlaylists: Future[Page[SimplePlaylist]] = {
    api.getWithOAuth[Page[SimplePlaylist]](s"${api.BASE_URL}/me/playlists")
  }

  def allMyPlaylists: Future[List[SimplePlaylist]] = {
    profiles.me.flatMap {
      my => api.getAll[SimplePlaylist](
        s => api.getWithOAuth[Page[SimplePlaylist]](s))(s"${api.BASE_URL}/users/${my.id}/playlists"
    )}
  }

}
