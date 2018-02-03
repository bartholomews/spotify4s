package it.turingtest.spotify.scala.client

import javax.inject.Inject
import scala.concurrent.Future

import com.vitorsvieira.iso.ISOCountry.ISOCountry
import it.turingtest.spotify.scala.client.entities._
import it.turingtest.spotify.scala.client.logging.AccessLogging

class SearchApi @Inject()(api: BaseApi) extends AccessLogging {

  /**
   * @see https://developer.spotify.com/web-api/search-item/
   */
  private final val SEARCH = s"${api.BASE_URL}/search"

  /**
   * Get Spotify catalog information about artists, albums, tracks or
   * playlists that match a keyword string.
   * @param q The search query's keywords (and optional field filters and
   *          operators), for example q=roadhouse%20blues.
   * @param itemType A comma-separated list of item types to search across.
   *                 Valid types are: album, artist, playlist, and track.
   * @param market An ISO 3166-1 alpha-2 country code or the string from_token.
   * @param limit The maximum number of results to return.
   * @param offset The index of the first result to return.
   */
  def search(
    q: String,
    itemType: ItemType,
    market: Option[ISOCountry] = None,
    limit: Option[Int] = None,
    offset: Option[Int] = None
  ): Future[SearchResults] = {
    val query: Seq[(String, String)] = Seq(
      "q" -> q,
      "type" -> itemType.value,
      "limit" -> limit.toString,
      "offset" -> offset.toString
    )
    itemType match {
      case ItemType.Album =>
        api.getWithToken[AlbumSearchResult](SEARCH, query.toList: _*)
      case ItemType.Artist =>
        api.getWithToken[ArtistSearchResult](SEARCH, query.toList: _*)
      case ItemType.Playlist =>
        api.getWithToken[PlaylistSearchResult](SEARCH, query.toList: _*)
      case ItemType.Track =>
        api.getWithToken[TrackSearchResult](SEARCH, query.toList: _*)
    }
  }

}
