package it.turingtest.spotify.scala.client

/**
  * Spotify API Scala Play wrapper
  */
import javax.inject.{Inject, Singleton}

import it.turingtest.spotify.scala.client.entities._
import it.turingtest.spotify.scala.client.logging.AccessLogging
import play.api.libs.json.{JsError, _}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class BaseApi(ws: WSClient, auth: AuthApi, val BASE_URL: String) extends AccessLogging {
  @Inject() def this(ws: WSClient, auth: AuthApi) = this(ws, auth, "https://api.spotify.com/v1")

  def getWithToken[T](endpoint: String, parameters: (String, String)*)(implicit fmt: Reads[T]): Future[T] = {
    withToken[T](t => validate[T] { get(endpoint, parameters)(t) }(fmt))
  }

  def getWithToken[T](read: String, endpoint: String, parameters: (String, String)*)(implicit fmt: Reads[T]): Future[T] = {
    withToken[T](t => validate[T](read) { get(endpoint, parameters)(t) }(fmt))
  }

  def getWithOAuth[T](endpoint: String, parameters: (String, String)*)(implicit fmt: Reads[T]): Future[T] = {
    withAuthToken()(t => validate[T] { get(endpoint, parameters)(t) }(fmt))
  }

  def getWithOAuth[T](read: String, endpoint: String, parameters: (String, String)*)(implicit fmt: Reads[T]): Future[T] = {
    withAuthToken()(t => validate[T](read) { get(endpoint, parameters)(t) }(fmt))
  }

  def getOptWithOAuth[T](endpoint: String, parameters: (String, String)*)(implicit fmt: Reads[T]): Future[Option[T]] = {
    withAuthToken()(t => validateOpt[T] { get(endpoint, parameters)(t) }(fmt))
  }

  def getOptWithOAuth[T](read: String, endpoint: String, parameters: (String, String)*)(implicit fmt: Reads[T]): Future[Option[T]] = {
    withAuthToken()(t => validateOpt[T](read) { get(endpoint, parameters)(t) }(fmt))
  }

  def get(endpoint: String, parameters: Seq[(String, String)]): Token => Future[WSResponse] = {
    t: Token => logResponse {
      ws.url(endpoint)
        .withHeaders(auth.bearer(t.access_token))
        .withQueryString(parameters.toList: _*)
        .get()
    }
  }

  def validate[T](f: Future[WSResponse])(implicit fmt: Reads[T]): Future[T] = {
    f map { response => validateJson(JsDefined(response.json)) } recoverWith { case ex => Future.failed(ex) }
  }

  def validate[T](read: String)(f: Future[WSResponse])(implicit fmt: Reads[T]): Future[T] = {
    f map { response => validateJson(response.json \ read) } recoverWith { case ex => Future.failed(ex) }
  }

  def validateOpt[T](f: Future[WSResponse])(implicit fmt: Reads[T]): Future[Option[T]] = {
    f map { response => validateJsonAsOpt(JsDefined(response.json)) } recoverWith { case ex => Future.failed(ex) }
  }

  def validateOpt[T](read: String)(f: Future[WSResponse])(implicit fmt: Reads[T]): Future[Option[T]] = {
    f map { response => validateJsonAsOpt(response.json \ read) } recoverWith { case ex => Future.failed(ex) }
  }

  private def validateJson[T](json: JsLookupResult)(implicit fmt: Reads[T]): T = {
    json.validate[T](fmt) match {
      case JsSuccess(obj, _) => obj
      case JsError(errors) => throw webApiException(json.as[JsValue], errors.head.toString)
    }
  }

  private def validateJsonAsOpt[T](json: JsLookupResult)(implicit fmt: Reads[T]): Option[T] = {
    json.validateOpt[T](fmt) match {
      case JsSuccess(obj, _) => obj
      case JsError(errors) => throw webApiException(json.as[JsValue], errors.head.toString)
    }
  }

  // TODO rate-limiting object from Retry-after header (@see https://developer.spotify.com/web-api/user-guide/#rate-limiting)
  private def webApiException(json: JsValue, error: String): WebApiException = {
    accessLogger.debug(json.toString)
    json.validate[RegularError] match {
      case JsSuccess(obj, _) => obj
      case JsError(_) => json.validate[AuthError] match {
        case JsSuccess(obj, _) => obj
        case JsError(_) => throw new Exception(s"$error")
      }
    }
  }

  @volatile private var authorization_code: Option[Future[Token]] = None
  @volatile private var client_credentials: Option[Future[Token]] = None

  @tailrec
  final def withToken[T](request: Token => Future[T]): Future[T] = {
    client_credentials match {
      case None => client_credentials = Some(refresh); withToken(request)
      case Some(t) => t flatMap { token =>
        client_credentials = if (token.expired) Some(refresh) else client_credentials
        request(token)
      }
    }
  }

  private def refresh: Future[Token] = validate[Token] { logResponse { auth.clientCredentials } }

  def setAuth(code: String): Future[Token] = {
    val token = access(code)
    authorization_code = Some(token)
    token
  }

  def setAuthAndThen[T](code: String)(request: Token => Future[T]): Future[T] = {
    authorization_code = Some(access(code))
    accessLogger.debug(s"authorization_code = ${authorization_code.isDefined}")
    withAuthToken(Some(code))(request)
  }

  @tailrec
  final def withAuthToken[T](authCode: Option[String] = None)(request: Token => Future[T]): Future[T] = {
    authorization_code match {
      case Some(t) => t flatMap { token => {
          authorization_code = { if (token.expired) Some(refresh(token)) else authorization_code }
          request(token)
        }
      }
      case None =>
        authorization_code = Some(access(authCode.getOrElse(throw new Exception("Authorization code not provided"))))
        withAuthToken(authCode)(request)
    }
  }

  private def refresh(oldToken: Token): Future[Token] = refresh(oldToken.refresh_token.get) map {
    newToken => Token(
      oldToken.access_token,
      newToken.token_type,
      newToken.scope,
      newToken.expires_in,
      oldToken.refresh_token
    )
  }

  private def access(code: String): Future[Token] = validate[Token] { logResponse { auth.accessToken(code) } }
  private def refresh(code: String): Future[Token] = validate[Token] { logResponse { auth.refreshToken(code) } }

}

