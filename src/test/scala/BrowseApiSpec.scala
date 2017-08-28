import java.util.Locale

import com.vitorsvieira.iso.ISOCountry
import it.turingtest.spotify.scala.client.entities._
import org.joda.time.LocalDateTime
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.play.guice.GuiceOneServerPerTest

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
class BrowseApiSpec extends FunSpec with Matchers with GuiceOneServerPerTest with SpotifyWebMock {

  /**
    * FEATURED PLAYLISTS ===============================================================================================
    */
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

    /**
      * NEW RELEASES ===================================================================================================
      */
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

    /**
      * CATEGORIES =====================================================================================================
      */
    describe("Categories endpoint") {

      it("should get a list of Categories with default parameters") {
        withBrowseApi { browseApi =>
          val result = await {
            browseApi.categories()
          }
          result.items.head.id shouldBe "toplists"
        }
      }

      it("should get a list of Categories with the specified parameters") {
        withBrowseApi { browseApi =>
          val result = await {
            browseApi.categories(
              locale = Some(new Locale("sv", ISOCountry.SWEDEN.value)),
              country = Some(ISOCountry.SWEDEN),
              limit = 10,
              offset = 5
            )
          }
          result.offset shouldBe 5
          result.total shouldBe 36
        }
      }

      it("should get a category with a specific id") {
        withBrowseApi { browseApi =>
          val result = await {
            browseApi.category("dinner")
          }
          result.name shouldBe "Dinner"
          result.id shouldBe "dinner"
          result.icons.head.height shouldBe Some(274)
        }
      }

      it("should get a category with a specific id, country and locale") {
        withBrowseApi { browseApi =>
          val result = await {
            browseApi.category(
              "dinner",
              Some(ISOCountry.SWEDEN),
              Some(new Locale("sv", ISOCountry.SWEDEN.value)))
          }
          result.name shouldBe "Middag"
          result.id shouldBe "dinner"
          result.icons.head.height shouldBe Some(274)
        }
      }

      it("should get playlists for a category id with offset") {
        withBrowseApi { browseApi =>
          val result = await {
            browseApi.categoryPlaylists(
              "party",
              offset = 10)
          }
          result.total shouldBe 17
          result.limit shouldBe 20
          result.offset shouldBe 10
          result.items.last.name shouldBe "Here Comes The Weekend! - by Spinnin' Records"
        }
      }

      it("should get playlists for a category id, country and limit") {
        withBrowseApi { browseApi =>
          val result = await {
            browseApi.categoryPlaylists(
              "party",
              Some(ISOCountry.BRAZIL), limit = 2)
          }
          result.total shouldBe 52
          result.limit shouldBe 2
          result.items.last.name shouldBe "Sexta"
        }
      }
    }

    /**
      * RECOMMENDATIONS ================================================================================================
      */
      // TODO fix unmarshaling error
    describe("Recommendations endpoint") {

      ignore("should get recommendations with market, seed_artists and seed_tracks") {
        withBrowseApi { browseApi =>
          val result = await {
            browseApi.getRecommendation(
              seed_tracks = Seq("0c6xIDDpzE81m2q797ordA"),
              market = Some(ISOCountry.UNITED_STATES),
              seed_artists = Seq("4NHQUGzhtTLFvgF5SZesLK")
            )
          }
          result.seeds.size shouldBe 1
        }
      }

      ignore("should get recommendations with min_popularity, min_energy, seed_tracks, market and seed_artists") {
        withBrowseApi { browseApi =>
          val result = await {
            browseApi.getRecommendation(
              popularity_range = (Some(50), None, None),
              energy_range = (Some(0.4f), None, None),
              seed_tracks = Seq("0c6xIDDpzE81m2q797ordA"),
              market = Some(ISOCountry.UNITED_STATES),
              seed_artists = Seq("4NHQUGzhtTLFvgF5SZesLK")
            )
          }
          result.seeds.size shouldBe 1
        }
      }

    }

  }

}
