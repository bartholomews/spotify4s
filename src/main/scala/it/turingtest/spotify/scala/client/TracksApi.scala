package it.turingtest.spotify.scala.client

import javax.inject.Inject

import it.turingtest.spotify.scala.client.entities.{AudioFeatures, Page, PlaylistTrack, Track}
import it.turingtest.spotify.scala.client.logging.AccessLogging
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @see https://developer.spotify.com/web-api/track-endpoints/
  */
class TracksApi @Inject()(configuration: play.api.Configuration, ws: WSClient, api: BaseApi) extends AccessLogging {

  private final def TRACKS(id: String) = s"${api.BASE_URL}/tracks/$id"

  // =====================================================================================================================
  /**
    * https://developer.spotify.com/web-api/get-track/
    */
  def getTrack(id: String): Future[Track] = api.get[Track](TRACKS(id))

  def getPlaylistTracks(href: String): Future[Page[PlaylistTrack]] = {
    api.getWithOAuth[Page[PlaylistTrack]](href)
  }

  def allTracks(href: String): Future[List[Track]] = allPlaylistTracks(href) map { p => p.map(pt => pt.track) }

  def allPlaylistTracks(href: String): Future[List[PlaylistTrack]] = {
    api.getAll[PlaylistTrack](href => getPlaylistTracks(href))(TRACKS(href))
  }

  // ===================================================================================================================
  /*
  private def getNewReleasesList(token: String, query: Option[String] = None): Future[WSResponse] = {
    ws.url(query.getOrElse(NEW_RELEASES))
      .withHeaders(auth_bearer(token))
      .withQueryString(
        "" -> "" // TODO
      )
      .get()
  }
  */

  // ===================================================================================================================

  /**
    *
    * @param track_id the Spotify ID for a track
    *           (@see https://developer.spotify.com/web-api/user-guide/#spotify-uris-and-ids)
    *
    * @return an AudioFeatures object for the track
    */
  def getAudioFeatures(track_id: String): Future[AudioFeatures] = {
    api.getWithOAuth[AudioFeatures](s"${api.BASE_URL}/audio-features/$track_id")
  }

  /**
    * @see https://developer.spotify.com/web-api/get-several-audio-features/
    *
    * @param tracks A list of the Spotify IDs for the tracks. Maximum: 100 IDs.
    *
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains an object whose key is "audio_features"
    *         and whose value is an array of audio features objects in JSON format.
    *         Objects are returned in the order requested.
    *         If an object is not found, a null value is returned in the appropriate position.
    *         Duplicate ids in the query will result in duplicate objects in the response.
    *         On error, the header status code is an error code and the response body contains an error object.
    *
    *
    */
  /* TODO maybe better to create a method zipping Seq(Track, Option[AudioFeatures])
  def getAudioFeatures(tracks: List[String]): Future[List[Option[AudioFeatures]]] = {
    api.withAuthToken()(t => {
      ws.url(s"${api.BASE_URL}/audio-features/?ids=${tracks.mkString(",")}")
        .withHeaders(api.auth_bearer(t.access_token))
        .get()
    } map { response =>
      val array = (response.json \ "audio_features").as[JsArray]
      val seq: Seq[Option[AudioFeatures]] = array.value.map(jsValue => {
        // case _: JsUndefined => None
        jsValue.validate[AudioFeatures].asOpt
      })
    })
  }
  */

}
