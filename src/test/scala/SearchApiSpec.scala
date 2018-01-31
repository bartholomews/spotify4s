import it.turingtest.spotify.scala.client.entities.{AlbumSearchResult, ArtistSearchResult, ItemType, PlaylistSearchResult, TrackSearchResult}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.play.guice.GuiceOneServerPerTest

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
class SearchApiSpec extends FunSpec with Matchers with GuiceOneServerPerTest with SpotifyWebMock with ScalaFutures {

  describe("Search Api") {

    it("should retrieve and parse search result for an album given a simple string") {
      withSearchApi { searchApi =>
        await {
          searchApi.search("tania bowra", ItemType.Album)
        } match {
          case AlbumSearchResult(albumPage) =>
            assert(albumPage.items.lengthCompare(1) == 0)
            assert(albumPage.items.head.name == "Place In The Sun")
            assert(albumPage.items.head.artists.lengthCompare(1) == 0)
            assert(albumPage.items.head.artists.head.name == "Tania Bowra")

          case other =>
            fail(s"Result should be a AlbumSearchResult, not `$other`")
        }
      }
    }

    it("should retrieve and parse search result for an artist given a simple string") {
      withSearchApi { searchApi =>
        await {
          searchApi.search("tania bowra", ItemType.Artist)
        } match {
          case ArtistSearchResult(artistPage) =>
            assert(artistPage.items.lengthCompare(1) == 0)
            assert(artistPage.items.head.name == "Tania Bowra")
            assert(artistPage.items.head.external_urls.spotify.contains(
              "https://open.spotify.com/artist/08td7MxkoHQkXnWAYD8d6Q"))

          case other =>
            fail(s"Result should be a ArtistSearchResult, not `$other`")
        }
      }
    }

    it("should retrieve and parse search result for a playlists given a simple string") {
      withSearchApi { searchApi =>
        await {
          // Poor Tania Bowra does not appear in a single playlist search...
          searchApi.search("ghibli", ItemType.Playlist)
        } match {
          case PlaylistSearchResult(playlistPage) =>
            assert(playlistPage.items.lengthCompare(20) == 0)
            assert(playlistPage.items.head.name == "Studio Ghibli (relaxing)")
            assert(playlistPage.items.head.owner.id == "lancer369")
            assert(playlistPage.items.head.tracks.total == 64)

          case other =>
            fail(s"Result should be a PlaylistSearchResult, not `$other`")
        }
      }
    }

    it("should retrieve and parse search result for tracks given a simple string") {
      withSearchApi { searchApi =>
        await {
          searchApi.search("tania bowra", ItemType.Track)
        } match {
          case TrackSearchResult(tracksPage) =>
            assert(tracksPage.items.lengthCompare(Math.min(tracksPage.total, tracksPage.limit)) == 0)
            assert(tracksPage.items.head.name == "All I Want")
            assert(tracksPage.items.head.popularity == 8)
            assert(tracksPage.items.head.duration_ms == 276773)
            assert(tracksPage.items.head.id.contains("2TpxZ7JUBn3uw46aR7qd6V"))

          case other =>
            fail(s"Result should be a TrackSearchResult, not `$other`")
        }
      }
    }
  }

}
