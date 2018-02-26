import org.scalatest.{FunSpec, Matchers, MustMatchers}
import org.scalatestplus.play.guice.GuiceOneServerPerTest

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
class AlbumsApiSpec extends FunSpec with GuiceOneServerPerTest with SpotifyWebMock {

  describe("Player Api") {

    describe("GET /v1/albums/$id") {

      it("should get an album") {
        withAlbumsApi { api =>
          val result = await { api.getAlbum(
            id = "0sNOF9WDwhWunNAHPD3Baj"
          ) }
          assert(result.name == "She's So Unusual")
        }
      }
    }

    describe("GET /v1/albums") {

      it("should get several album") {
        withAlbumsApi { api =>
          val result = await { api.getAlbums(
            ids = Seq(
              "41MnTivkwTO3UUJ8DrqEJJ",
              "6JWc4iAiJ9FjyK0B59ABb4",
              "6UXCm6bOO4gFlDQZV5yL37")
          ) }
          assert(result.lengthCompare(1) == 0)
          assert(result.head.name == "The Best Of Keane (Deluxe Edition)")
        }
      }
    }

    describe("GET /v1/albums/$id/tracks") {

      it("should get album tracks") {

        withAlbumsApi { api =>
          val result = await { api.getAlbumTracks(
            id = "6akEvsycLGftJxYudPjmqK",
            limit = Some(2)
          )}
          assert(result.total == 11)
        }

      }

    }

  }

}
