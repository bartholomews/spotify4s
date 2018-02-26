import java.nio.charset.StandardCharsets
import java.util.Base64

import it.turingtest.spotify.scala.client._
import play.api.Configuration
import play.api.mvc._
import play.api.test.WsTestClient
import play.core.server.Server
import play.api.routing.sird._
import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration._

import play.api.http.{DefaultFileMimeTypes, FileMimeTypes, FileMimeTypesConfiguration}

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
trait SpotifyWebMock {

  implicit val fileMimeTypes: FileMimeTypes =
    new DefaultFileMimeTypes(FileMimeTypesConfiguration(Map(
      "json" -> "application/json",
      "xml" -> "application/xml"
    )))

  val CLIENT_ID = "some-client-id"
  val CLIENT_SECRET = "some-client-secret"
  val REDIRECT_URI = "some-redirect-uri"

  implicit val config: Configuration = Configuration.apply(
    ("CLIENT_ID", CLIENT_ID),
    ("CLIENT_SECRET", CLIENT_SECRET),
    ("REDIRECT_URI", REDIRECT_URI)
  )

  private val routes: PartialFunction[RequestHeader, Handler] = {
    case POST(p"/api/token") => Action(BodyParsers.parse.tolerantFormUrlEncoded) { request =>
      authError(request.headers).getOrElse(
        request.body get "grant_type" match {
          case Some(Seq("client_credentials")) => Results.Ok.sendResource("auth/client_credentials.json")
          case Some(Seq("authorization_code")) => authCodeResponse(request.body)
          case Some(Seq("refresh_token")) => refreshTokenResponse(request.body)
          case _ => Results.BadRequest.sendResource("auth/unsupported_grant.json")
        }
      )
    }
      // TODO should validate headers for authError depending on endpoint
    case GET(endpoint) => Action { sendResource(endpoint) }
    case _ => Action { Results.BadRequest }
  }

  private def authError(headers: Headers): Option[Result] = {
    headers.get("Authorization") match {
      case None => Some(Results.BadRequest.sendResource("auth/invalid_client_no_authorization_header.json"))
      case Some(client) =>
        val result = new String(Base64.getDecoder.decode(client.drop(6).getBytes(StandardCharsets.UTF_8)))
        result.split(":") match {
          case Array(CLIENT_ID, CLIENT_SECRET) => None
          case Array(CLIENT_ID, _) => Some(Results.BadRequest.sendResource("auth/invalid_client_secret.json"))
          case _ => Some(Results.BadRequest.sendResource("auth/invalid_client.json"))
        }
      }
  }

  private def authCodeResponse(body: Map[String, Seq[String]]) = {
    body get "code" match {
      case Some(Seq("valid_code")) => Results.Ok.sendResource("auth/authorization_access.json")
      case Some(_) => Results.BadRequest.sendResource("auth/authorization_invalid_code.json")
      case None => Results.BadRequest.sendResource("auth/authorization_no_code.json")
    }
  }

  private def refreshTokenResponse(body: Map[String, Seq[String]]) = body get "refresh_token" match {
    case Some(Seq("refresh-token")) => Results.Ok.sendResource("auth/authorization_refresh.json")
    case Some(_) => Results.BadRequest.sendResource("auth/authorization_refresh_invalid_token.json")
    case None => Results.BadRequest.sendResource("auth/authorization_refresh_no_token.json")
  }

  /**
    * Json resources should map the real Spotify endpoints with relative path
    * and filename mapping the full request, including querystring,
    * just escaping question mark with "%3F" and further slashes; e.g. the following request:
    * "/browse/featured-playlists?country=SE&limit=2&offset=20" will look for a json file at:
    * "test/resources/browse/featured-playlists%3Fcountry=SE&limit=2&offset=0.json"
    *
    * NOTE: remember that if a request has default values, those need to be included in
    * the resource filename even if not explicitly defined by the test call
    * (e.g. BrowseApi.featuredPlaylists once called with a parameter will set limit to 20 and offset to 0)
    * Also, with this approach unfortunately the order of the query parameters have to match, so you should construct
    * test resources filenames with parameters in the order created by the specific method call.
    */
  private def sendResource(endpoint: RequestHeader) = {
    Results.Ok.sendResource(
      s"${
        endpoint.uri.drop(1)
          .replace("?", "%3F")
          .replaceAll("%3A", ":")
          .replaceAll("%2C", ",")
          .replaceAll("%28", "(")
          .replaceAll("%29", ")")
          .replaceAll("%C3%B1", "ñ")
      }.json")
  }

  def await[T](block: Awaitable[T]): T = Await.result(block, 5.seconds)

  def withAuthApi[T](block: AuthApi => T)(implicit config: Configuration):  T = {
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

  def withBrowseApi[T](block: BrowseApi => T): T = {
    Server.withRouter() { routes } { implicit port =>
      WsTestClient.withClient { client =>
        val authApi = new AuthApi(config, client, "")
        val baseApi = new BaseApi(client, authApi, "")
        block(new BrowseApi(baseApi))
      }
    }
  }

  def withAlbumsApi[T](block: AlbumsApi => T): T = {
    Server.withRouter() { routes } { implicit port =>
      WsTestClient.withClient { client =>
        val authApi = new AuthApi(config, client, "")
        val baseApi = new BaseApi(client, authApi, "")
        block(new AlbumsApi(baseApi))
      }
    }
  }

  def withPlayerApi[T](block: PlayerApi => T): T = {
    Server.withRouter() { routes } { implicit port =>
      WsTestClient.withClient { client =>
        val authApi = new AuthApi(config, client, "")
        val baseApi = new BaseApi(client, authApi, "")
        baseApi.setAuth("valid_code") // set valid oAuth
        block(new PlayerApi(client, baseApi))
      }
    }
  }

  def withPlaylistsApi[T](block: PlaylistsApi => T): T = {
    Server.withRouter() { routes } { implicit port =>
      WsTestClient.withClient { client =>
        val authApi = new AuthApi(config, client, "")
        val baseApi = new BaseApi(client, authApi, "")
        baseApi.setAuth("valid_code") // set valid oAuth
        val profilesApi = new ProfilesApi(client, baseApi)
        block(new PlaylistsApi(client, baseApi, profilesApi))
      }
    }
  }

  def withProfilesApi[T](block: ProfilesApi => T): T = {
    Server.withRouter() { routes } { implicit port =>
      WsTestClient.withClient { client =>
        val authApi = new AuthApi(config, client, "")
        val baseApi = new BaseApi(client, authApi, "")
        baseApi.setAuth("valid_code") // set valid oAuth
        block(new ProfilesApi(client, baseApi))
      }
    }
  }

  def withSearchApi[T](block: SearchApi => T): T = {
    Server.withRouter() { routes } { implicit port =>
      WsTestClient.withClient { client =>
        val authApi = new AuthApi(config, client, "")
        val baseApi = new BaseApi(client, authApi, "")
        baseApi.setAuth("valid_code") // set valid oAuth
        block(new SearchApi(baseApi))
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

}
