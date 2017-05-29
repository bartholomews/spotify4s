import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.{Action, Handler, RequestHeader, Results}
import play.api.test.WsTestClient
import play.core.server.Server
import play.api.routing.sird._

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
trait SpotifyWebMock extends {

  private val config = Configuration.apply(
    ("CLIENT_ID", "some-client-id"),
    ("CLIENT_SECRET", "some-client-secret"),
    ("REDIRECT_URI", "some-redirect-uri")
  )

  private val routes: PartialFunction[RequestHeader, Handler] = {
    case POST(p"/api/token") => Action { // TODO more specific depending on body, also for failures and oAuth
      Results.Ok.sendResource("auth/client_credentials.json")
    }
    case GET(p"/tracks/3n3Ppam7vgaVa1iaRUc9Lp") => Action {
      Results.Ok.sendResource("tracks/3n3Ppam7vgaVa1iaRUc9Lp.json")
    }
  }

  def withAuthApi[T](block: AuthApi => T):  T = {
    Server.withRouter() { routes } { implicit port =>
      WsTestClient.withClient { client =>
        block(new AuthApi(config, client, ""))
      }
    }
  }

  def withBaseApi[T](block: BaseApi => T): T = {
    Server.withRouter() { routes } { implicit port =>
      WsTestClient.withClient { client =>
        val auth = new AuthApi(config, client, "")
        block(new BaseApi(client, auth, ""))
      }
    }
  }

  def withTracksApi[T](block: TracksApi => T): T = {
    Server.withRouter() { routes } { implicit port =>
      WsTestClient.withClient { client =>
        val authApi = new AuthApi(config, client, "")
        val baseApi = new BaseApi(client, authApi, "")
        block(new TracksApi(client, baseApi))
      }
    }
  }

}
