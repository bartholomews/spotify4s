import it.turingtest.spotify.scala.client.entities.Token
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.play.guice.GuiceOneServerPerTest

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
class AuthApiSpec extends FunSpec with Matchers with GuiceOneServerPerTest with SpotifyWebMock {

  describe("Auth Api") {

    it("should receive an access token on a client credentials request") {

      withAuthApi { auth =>
        val result = await {
          auth.clientCredentials
        }
        result.status shouldBe 200
        val token = result.json.validate[Token].get
        token.access_token shouldBe "some-access-token"
      }
    }

  }

}
