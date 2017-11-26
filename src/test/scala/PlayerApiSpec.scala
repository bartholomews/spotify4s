import com.vitorsvieira.iso.ISOCountry
import it.turingtest.spotify.scala.client.entities._
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.play.guice.GuiceOneServerPerTest

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
class PlayerApiSpec extends FunSpec with Matchers with GuiceOneServerPerTest with SpotifyWebMock {

  describe("Player Api") {

    describe("GET /v1/me/player/devices") {

      it("should get a device") {
        withPlayerApi { api =>
          val result = await { api.devices }
          result.size shouldBe 1
          val device = result.head
          device.name shouldBe "My fridge"
        }
      }
    }

    describe("GET /v1/me/player") {

      it("should get a playing device") {
        withPlayerApi { api =>
          val result = await { api.lastPlayback() }
          result.isDefined shouldBe true
          val playing = result.get
          playing.device.deviceType shouldBe "Smartphone"
          playing.repeat_state shouldBe REPEAT_OFF
          playing.is_shuffle_on shouldBe false
          playing.context.get.contextType shouldBe PlaylistContext
        }
      }
    }

  }

}
