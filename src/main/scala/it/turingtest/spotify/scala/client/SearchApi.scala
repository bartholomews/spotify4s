package it.turingtest.spotify.scala.client

import javax.inject.Inject
import scala.concurrent.Future

import com.vitorsvieira.iso.ISOCountry.ISOCountry
import it.turingtest.spotify.scala.client.entities._
import it.turingtest.spotify.scala.client.logging.AccessLogging
import play.api.libs.json.{JsError, JsString, JsSuccess, Json, Reads}

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
    itemType: SearchApi.ItemType,
    market: Option[ISOCountry] = None,
    limit: Option[Int] = None,
    offset: Option[Int] = None
  ): Future[SearchApi.SearchResults] = {
    val query: Seq[(String, String)] = Seq(
      "q" -> q,
      "type" -> itemType.value,
      "limit" -> limit.toString,
      "offset" -> offset.toString
    )
    itemType match {
      case SearchApi.ItemType.Album =>
        api.getWithToken[SearchApi.AlbumSearchResult](SEARCH, query.toList: _*)
      case SearchApi.ItemType.Artist =>
        api.getWithToken[SearchApi.ArtistSearchResult](SEARCH, query.toList: _*)
      case SearchApi.ItemType.Playlist =>
        api.getWithToken[SearchApi.PlaylistSearchResult](SEARCH, query.toList: _*)
      case SearchApi.ItemType.Track =>
        api.getWithToken[SearchApi.TrackSearchResult](SEARCH, query.toList: _*)
    }
  }

}

object SearchApi {

  sealed trait ItemType {
    def value: String
  }

  object ItemType {
    case object Album extends ItemType { def value: String = "album" }
    case object Artist extends ItemType { def value: String = "artist" }
    case object Playlist extends ItemType { def value: String = "playlist" }
    case object Track extends ItemType { def value: String = "track" }
    
    implicit val reader: Reads[ItemType] = {
      case JsString("album") => JsSuccess(ItemType.Album)
      case JsString("artist") => JsSuccess(ItemType.Artist)
      case JsString("playlist") => JsSuccess(ItemType.Playlist)
      case JsString("track") => JsSuccess(ItemType.Track)
      case other => JsError(s"Cannot parse ItemType from json '$other'")
    }
  }

  sealed trait SearchResults

  case class AlbumSearchResult(albums: Page[SimpleAlbum]) extends SearchResults
  object AlbumSearchResult {
    implicit val reader: Reads[AlbumSearchResult] = Json.reads[AlbumSearchResult]
  }

  case class ArtistSearchResult(artists: Page[SimpleArtist]) extends SearchResults
  object ArtistSearchResult {
    implicit val reader: Reads[ArtistSearchResult] = Json.reads[ArtistSearchResult]
  }

  case class PlaylistSearchResult(playlists: Page[SimplePlaylist]) extends SearchResults
  object PlaylistSearchResult {
    implicit val reader: Reads[PlaylistSearchResult] = Json.reads[PlaylistSearchResult]
  }

  case class TrackSearchResult(tracks: Page[Track]) extends SearchResults
  object TrackSearchResult {
    implicit val reader: Reads[TrackSearchResult] = Json.reads[TrackSearchResult]
  }

}
