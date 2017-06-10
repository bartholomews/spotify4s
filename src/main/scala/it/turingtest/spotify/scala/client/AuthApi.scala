package it.turingtest.spotify.scala.client

/**
  *
  */
import javax.inject.{Inject, Singleton}

import it.turingtest.spotify.scala.client.entities._
import it.turingtest.spotify.scala.client.logging.AccessLogging
import it.turingtest.spotify.scala.client.utils.ConversionUtils
import play.api.Configuration
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.Future

@Singleton
class AuthApi(configuration: Configuration, ws: WSClient, baseUrl: String) extends AccessLogging {
  @Inject() def this(conf: Configuration, ws: WSClient) = this(conf, ws, "https://accounts.spotify.com")

  val AUTHORIZE_ENDPOINT = s"$baseUrl/authorize"
  val TOKEN_ENDPOINT = s"$baseUrl/api/token"

  private val CLIENT_ID = configuration.underlying.getString("CLIENT_ID")
  private val CLIENT_SECRET = configuration.underlying.getString("CLIENT_SECRET")
  private val REDIRECT_URI = configuration.underlying.getString("REDIRECT_URI")

  def clientCredentials: Future[WSResponse] = {
    ws.url(TOKEN_ENDPOINT)
      .withHeaders(auth_headers)
      .post(Map("grant_type" -> Seq("client_credentials")))
  }

  def accessToken(code: String): Future[WSResponse] = {
    ws.url(TOKEN_ENDPOINT)
      .withHeaders(auth_headers)
      .post(Map(
        "grant_type" -> Seq("authorization_code"),
        "code" -> Seq(code),
        "redirect_uri" -> Seq(REDIRECT_URI)
      ))
  }

  def refreshToken(refreshToken: String): Future[WSResponse] = {
    ws.url(TOKEN_ENDPOINT)
      .withHeaders(auth_headers)
      .post(Map(
        "grant_type" -> Seq("refresh_token"),
        "refresh_token" -> Seq(refreshToken)
      ))
  }

  def bearer(token: String): (String, String) = {
    "Authorization" -> s"Bearer $token"
  }

  private val auth_headers = {
    val base64_secret = ConversionUtils.base64(s"$CLIENT_ID:$CLIENT_SECRET")
    "Accept" -> "application/json"
    "Authorization" -> s"Basic $base64_secret"
  }

  def authoriseURL: String = authoriseURL()

  def authoriseURL(state: Option[String] = None, scopes: List[Scope] = List(), showDialog: Boolean = true): String = {
    requestAuthoriseURL(CLIENT_ID, REDIRECT_URI, state, scopes, showDialog).uri.toString
  }

  /**
    * @see https://developer.spotify.com/web-api/authorization-guide/
    *
    * @param client_id    Required. The client ID provided to you by Spotify when you register your application.
    *
    * @param redirect_uri Required. The URI to redirect to after the user grants/denies permission.
    *                     This URI needs to have been entered in the Redirect URI whitelist
    *                     that you specified when you registered your application.
    *                     The value of redirect_uri here must exactly match one of the values
    *                     you entered when you registered your application,
    *                     including upper/lowercase, terminating slashes, etc.
    *
    * @param state        Optional, but strongly recommended. The state can be useful for correlating requests and responses.
    *                     Because your redirect_uri can be guessed, using a state value can increase your assurance
    *                     that an incoming connection is the result of an authentication request.
    *                     If you generate a random string or encode the hash of some client state (e.g., a cookie)
    *                     in this state variable, you can validate the response to additionally ensure
    *                     that the request and response originated in the same browser.
    *                     This provides protection against attacks such as cross-site request forgery.
    *
    * @see               RFC-6749 [https://tools.ietf.org/html/rfc6749#section-10.12]
    *
    * @param scopes      Optional. A space-separated list of scopes: @see `Scope`
    *                    If no scopes are specified, authorization will be granted only
    *                    to access publicly available information: that is, only information
    *                    normally visible in the Spotify desktop, web and mobile players.
    *
    * @param show_dialog Optional. Whether or not to force the user to approve the app again if they’ve already done so.
    *                    If false (default), a user who has already approved the application
    *                    may be automatically redirected to the URI specified by redirect_uri.
    *                    If true, the user will not be automatically redirected and will have to approve the app again.
    *
    * @return the Spotify URL where the user can grant/deny permissions.
    */
  private def requestAuthoriseURL(client_id: String, redirect_uri: String, state: Option[String],
                                  scopes: List[Scope], show_dialog: Boolean): WSRequest = {

    ws.url(AUTHORIZE_ENDPOINT)
      .withHeaders(auth_headers)
      .withQueryString(
        "client_id" -> client_id,
        "response_type" -> "code",
        "redirect_uri" -> REDIRECT_URI,
        "scope" -> scopes.map(s => s.value).mkString(" "),
        "show_dialog" -> show_dialog.toString,
        "state" -> state.getOrElse("")
      )
  }

}

