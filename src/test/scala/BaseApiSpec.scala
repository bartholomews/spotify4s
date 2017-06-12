import it.turingtest.spotify.scala.client.entities.Track
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.play.guice.GuiceOneServerPerTest

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
class BaseApiSpec extends FunSpec with Matchers with GuiceOneServerPerTest with SpotifyWebMock {

  describe("Base Api") {

    it("should be able to get a resource") {
      withBaseApi { api =>
        val result = await { api.get[Track]("/tracks/3n3Ppam7vgaVa1iaRUc9Lp") }
        result.id shouldBe Some("3n3Ppam7vgaVa1iaRUc9Lp")
      }
    }
  }

}
