import com.vitorsvieira.iso.ISOCountry
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.play.guice.GuiceOneServerPerTest

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
class TracksApiSpec extends FunSpec with Matchers with GuiceOneServerPerTest with SpotifyWebMock with ScalaFutures {

  describe("Tracks Api") {

    it("should retrieve and parse AudioAnalysis for a Track") {
      withTracksApi { tracksApi =>
        val result = await {
          tracksApi.getAudioAnalysis("06AKEBrKUckW0KREUWRnvT")
        }
        result.bars.head.start shouldBe 0.06443f
      }
    }

    it("should retrieve and parse AudioFeatures for a Track") {
      withTracksApi { tracksApi =>
        val result = await {
          tracksApi.getAudioFeatures("06AKEBrKUckW0KREUWRnvT")
        }
        result.time_signature shouldBe 4
      }
    }

    it("should retrieve and parse AudioFeatures for a sequence of Tracks") {
      withTracksApi { tracksApi =>
        val result = await {
          tracksApi.getAudioFeatures(Seq(
            "4JpKVNYnVcJ8tuMKjAj50A", "2NRANZE9UCmPAS5XVbXL40", "24JygzOLM0EmRQeGtFcIcG"))
        }
        result.size shouldBe 3
      }
    }

    it("should retrieve and parse a Track correctly") {
      withTracksApi { tracksApi =>
        val result = await {
          tracksApi.getTrack("3n3Ppam7vgaVa1iaRUc9Lp")
        }
        result.id shouldBe Some("3n3Ppam7vgaVa1iaRUc9Lp")
        result.album.name shouldBe "Hot Fuss (Deluxe Version)"
      }
    }

    it("should retrieve and parse a Track with a specific market") {
      withTracksApi { tracksApi =>
        val result = await {
          tracksApi.getTrack("3n3Ppam7vgaVa1iaRUc9Lp", market = Some(ISOCountry.ITALY))
        }
        result.id shouldBe Some("3n3Ppam7vgaVa1iaRUc9Lp")
        result.album.name shouldBe "Hot Fuss (Deluxe Version)"
        result.album.available_markets shouldBe Nil
      }
    }

    it("should retrieve and parse a sequence of Tracks correctly") {
      withTracksApi { tracksApi =>
        val result = await {
          tracksApi.getTracks(Seq("3n3Ppam7vgaVa1iaRUc9Lp", "3twNvmDtFQtAd5gMKedhLD"))
        }
        result.size shouldBe 2
        result.head.id shouldBe Some("3n3Ppam7vgaVa1iaRUc9Lp")
        result.head.name shouldBe "Mr. Brightside"
        result.head.album.name shouldBe "Hot Fuss (Deluxe Version)"
        result.last.id shouldBe Some("3twNvmDtFQtAd5gMKedhLD")
        result.last.name shouldBe "Somebody Told Me"
        result.last.album.name shouldBe "Hot Fuss (Deluxe Version)"
      }
    }

    it("should retrieve and parse a sequence of Tracks with a specific market") {
      withTracksApi { tracksApi =>
        val result = await {
          tracksApi.getTracks(
            Seq("3n3Ppam7vgaVa1iaRUc9Lp", "3twNvmDtFQtAd5gMKedhLD"),
            market = Some(ISOCountry.SPAIN))
        }
        result.size shouldBe 2
      }
    }

  }

}
