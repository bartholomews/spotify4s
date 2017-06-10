import it.turingtest.spotify.scala.client.BrowseApi
import it.turingtest.spotify.scala.client.entities.{SimplePlaylist, SimpleTrack, Token, Track}
import org.scalatest.{FunSpec, Matchers, ShouldMatchers}

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration._

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
class SpotifyClientSpec extends FunSpec with SpotifyWebMock with Matchers with ShouldMatchers {

  describe("Spotify Client") {

    describe("Auth Api") {

      it("should receive an access token on a client credentials request") {
        withAuthApi { auth =>
          val result = await { auth.clientCredentials }
          result.status shouldBe 200
          val token = result.json.validate[Token].get
          token.access_token shouldBe "some-access-token"
        }
      }
    }

    describe("Base Api") {

      it("should be able to get a resource") {
        withBaseApi { api =>
          val result = await { api.get[Track]("/tracks/3n3Ppam7vgaVa1iaRUc9Lp") }
          result.id shouldBe Some("3n3Ppam7vgaVa1iaRUc9Lp")
        }
      }
    }

    describe("Tracks Api") {

      it("should retrieve and parse a Track correctly") {

        withTracksApi { tracksApi =>
          val result = await { tracksApi.getTrack("3n3Ppam7vgaVa1iaRUc9Lp") }
          result.id shouldBe Some("3n3Ppam7vgaVa1iaRUc9Lp")
        }
      }

    }

    describe("Browse Api") {

      it("should retrieve a FeaturedPlaylist object") {

        withBrowseApi { browseApi =>
          val result = await { browseApi.featuredPlaylists }
          result.message shouldBe "Hur är ditt torsdagshumör?"
        }
      }

    }

  }

}
