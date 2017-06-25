import com.vitorsvieira.iso.ISOCountry
import it.turingtest.spotify.scala.client.entities._
import org.joda.time.LocalDateTime
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.play.guice.GuiceOneServerPerTest

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
class BrowseApiSpec extends FunSpec with Matchers with GuiceOneServerPerTest with SpotifyWebMock {

  describe("Featured Playlists endpoint") {

    it("should retrieve a FeaturedPlaylist object") {
      withBrowseApi { browseApi =>
        val result = await {
          browseApi.featuredPlaylists
        }
        result.message shouldBe "Easy like Sunday morning."
      }
    }

    it("should retrieve featured playlists from a request with country and limit query") {
      withBrowseApi { browseApi =>
        val result = await {
          browseApi.featuredPlaylists(
            country = Some(ISOCountry.SWEDEN),
            limit = 2
          )
        }
        result.message shouldBe "Monday morning music, coming right up!"
        result.playlists.limit shouldBe 2
      }
    }

    it("should retrieve featured playlists from a request with timestamp and offset query") {
      withBrowseApi { browseApi =>
        val result = await {
          browseApi.featuredPlaylists(
            country = Some(ISOCountry.UNITED_STATES),
            timestamp = Some(new LocalDateTime(2014, 10, 23, 15, 0, 0)),
            offset = 2
          )
        }
        result.message shouldBe "Celebrating Pride and Black Music Month!"
        result.playlists.offset shouldBe 2
        result.playlists.previous shouldBe defined
      }
    }

    describe("New Releases endpoint") {

      it("should get a list of new releases with default parameters") {
        withBrowseApi { browseApi =>
          val result = await {
            browseApi.newReleases
          }
          result.albums shouldBe a[Page[_]]
          result.albums.href shouldBe "https://api.spotify.com/v1/browse/new-releases?offset=0&limit=20"
          result.albums.limit shouldBe 20
        }
      }

      it("should get a list of new releases with the specified parameters") {
        withBrowseApi { browseApi =>
          val result = await {
            browseApi.newReleases(
              country = Some(ISOCountry.UNITED_KINGDOM),
              limit = 2,
              offset = 1
            )
          }
          result.albums shouldBe a[Page[_]]
          result.albums.href shouldBe "https://api.spotify.com/v1/browse/new-releases?country=GB&offset=1&limit=2"
          result.albums.limit shouldBe 2
          result.albums.offset shouldBe 1
          result.albums.previous shouldBe defined
        }
      }
    }

  }


}
