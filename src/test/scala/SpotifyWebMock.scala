import it.turingtest.spotify.scala.client.{AuthApi, BaseApi, BrowseApi, TracksApi}
import play.api.Configuration
import play.api.mvc.{Action, Handler, RequestHeader, Results}
import play.api.test.WsTestClient
import play.core.server.Server
import play.api.routing.sird._

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration._

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
trait SpotifyWebMock {

  private val config = Configuration.apply(
    ("CLIENT_ID", "some-client-id"),
    ("CLIENT_SECRET", "some-client-secret"),
    ("REDIRECT_URI", "some-redirect-uri")
  )

  private val routes: PartialFunction[RequestHeader, Handler] = {
    case POST(p"/api/token") => Action { // TODO more specific depending on body, also for failures and oAuth
      Results.Ok.sendResource("auth/client_credentials.json")
    }
    case GET(endpoint) => Action { sendResource(endpoint) }
    case _ => Action { Results.BadRequest }
  }

  /**
    * Json resources should map the real Spotify endpoints with relative path
    * and filename mapping the full request, including querystring,
    * just escaping question mark with "%3F"; e.g. the following request:
    * "/browse/featured-playlists?country=SE&limit=2&offset=20" will look for a json file at:
    * "test/resources/browse/featured-playlists%3Fcountry=SE&limit=2&offset=0.json"
    *
    * NOTE: remember that if a request has default values, those need to be included in
    * the resource filename even if not explicitly defined by the test call
    * (e.g. BrowseApi.featuredPlaylists once called with a parameter will set limit to 20 and offset to 0)
    */
  private def sendResource(endpoint: RequestHeader) = {
    try {Results.Ok.sendResource(
      s"${endpoint.uri.drop(1)
        .replace("?", "%3F")
        .replaceAll("%3A", ":")
      }.json")
    }
    catch { // TODO see why when not found it still throws a fasterxml JsonParseException
      case _: Throwable => throw new Exception(s"No resource found for endpoint $endpoint")
    }
  }

  def await[T](block: Awaitable[T]): T = Await.result(block, 3.seconds)

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
        block(new TracksApi(baseApi))
      }
    }
  }

  def withBrowseApi[T](block: BrowseApi => T): T = {
    Server.withRouter() { routes } { implicit port =>
      WsTestClient.withClient { client =>
        val authApi = new AuthApi(config, client, "")
        val baseApi = new BaseApi(client, authApi, "")
        block(new BrowseApi(baseApi))
      }
    }
  }

}
