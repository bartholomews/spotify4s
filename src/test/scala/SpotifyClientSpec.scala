import it.turingtest.spotify.scala.client.entities.Token
import org.scalatest.{FlatSpec, Matchers, ShouldMatchers}
import play.api.Configuration
import play.api.libs.json._
import play.api.mvc._
import play.api.routing.sird._
import play.api.test._
import play.core.server.Server

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
class SpotifyClientSpec extends FlatSpec with Matchers with ShouldMatchers {

  val config = Configuration.apply(
    ("CLIENT_ID", "some-client-id"),
    ("CLIENT_SECRET", "some-client-secret"),
    ("REDIRECT_URI", "some-redirect-uri")
  )

  // TODO

  "SpotifyClient" should "not fail" in {

      Server.withRouter() {
        case GET(p"/me") => Action {
          Results.Ok(Json.arr(Json.obj("full_name" -> "octocat/Hello-World")))
        }
      } { implicit port =>
        WsTestClient.withClient { client =>

          val b = new BaseApi(
            config,
            client,
            "")

          b.callback("some_auth_code")(_ => Future())

          val result = Await.result(
            new ProfilesApi(
              client,
              b
            ).me, 10.seconds)

          result shouldBe 1

        }
      }

    }

}
