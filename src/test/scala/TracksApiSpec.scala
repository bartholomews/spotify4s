import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.play.guice.GuiceOneServerPerTest

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
class TracksApiSpec extends FunSpec with Matchers with GuiceOneServerPerTest with SpotifyWebMock {

  describe("Tracks Api") {

    it("should retrieve and parse a Track correctly") {

      withTracksApi { tracksApi =>
        val result = await { tracksApi.getTrack("3n3Ppam7vgaVa1iaRUc9Lp") }
        result.id shouldBe Some("3n3Ppam7vgaVa1iaRUc9Lp")
      }
    }

  }

}
