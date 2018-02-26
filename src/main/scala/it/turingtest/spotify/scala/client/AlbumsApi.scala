package it.turingtest.spotify.scala.client

import javax.inject.Inject

import com.vitorsvieira.iso.ISOCountry.ISOCountry
import it.turingtest.spotify.scala.client.entities._
import it.turingtest.spotify.scala.client.logging.AccessLogging
import it.turingtest.spotify.scala.client.utils.ConversionUtils

import scala.concurrent.Future

/**
  * @see https://developer.spotify.com/web-api/album-endpoints/
  */
class AlbumsApi @Inject()(api: BaseApi) extends AccessLogging {

  // ===================================================================================================================

  /**
    * Get an album
    *
    * @see https://developer.spotify.com/web-api/get-album/
    *
    * @param id The Spotify ID for the album.
    *
    * @param market Optional. An ISO 3166-1 alpha-2 country code.
    *               Provide this parameter if you want to apply Track Relinking.
    *
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains an album object in JSON format.
    *         On error, the header status code is an error code and the response body contains an error object.
    */
  def getAlbum(id: String, market: Option[ISOCountry] = None): Future[FullAlbum] = {
    val query: Seq[(String, String)] = ConversionUtils.seq(("market", market))
    api.getWithToken[FullAlbum](s"${api.BASE_URL}/albums/$id", query: _*)
  }

  /**
    * Get several albums
    *
    * @see https://developer.spotify.com/web-api/get-several-albums/
    *
    * @param ids Required. A sequence of the Spotify IDs for the albums.
    *            Maximum: 20 IDs.
    *
    * @param market Optional. An ISO 3166-1 alpha-2 country code.
    *               Provide this parameter if you want to apply Track Relinking.
    *
    * @return On success, the HTTP status code in the response header
    *         is 200 OK and the response body contains an object
    *         whose key is "albums" and whose value is an array
    *         of album object in JSON format. Objects are returned
    *         in the order requested. If an object is not found,
    *         an empty value is returned in the appropriate position.
    *         Duplicate ids in the query will result in duplicate objects
    *         in the response. On error, the header status code is an error code
    *         and the response body contains an error object.
    */
  def getAlbums(ids: Seq[String], market: Option[ISOCountry] = None): Future[Seq[FullAlbum]] = {
    val query: Seq[(String, String)] = ("ids", ids.mkString(",")) +: ConversionUtils.seq(("market", market))
    api.getWithToken[Seq[FullAlbum]]("albums", s"${api.BASE_URL}/albums/", query: _*)
  }

  /**
    * Get an album's track
    *
    * @see https://developer.spotify.com/web-api/get-albums-tracks/
    *
    * @param id The Spotify ID for the album.
    *
    * @param limit Optional. The maximum number of tracks to return.
    *              Default: 20. Minimum: 1. Maximum: 50.
    *
    * @param offset Optional. The index of the first track to return.
    *               Default: 0 (the first object).
    *               Use with limit to get the next set of tracks.
    *
    * @param market Optional. An ISO 3166-1 alpha-2 country code.
    *               Provide this parameter if you want to apply Track Relinking.
    *
    * @return On success, the HTTP status code in the response header
    *         is 200 OK and the response body contains an array of
    *         simplified track objects (wrapped in a paging object)
    *         in JSON format. On error, the header status code
    *         is an error code and the response body contains an error object.
    */
  def getAlbumTracks(id: String, limit: Option[Int] = Some(20),
                     offset: Option[Int] = Some(0),
                     market: Option[ISOCountry] = None): Future[Page[SimpleTrack]] = {

    val query: Seq[(String, String)] = ConversionUtils.seq(
      ("limit", limit), ("offset", offset), ("market", market)
    )
    api.getWithToken[Page[SimpleTrack]](s"${api.BASE_URL}/albums/$id/tracks", query: _*)
  }

  // ===================================================================================================================


}
