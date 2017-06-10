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
class BaseApi(ws: WSClient, auth: AuthApi, baseUrl: String) extends AccessLogging {
  @Inject() def this(ws: WSClient, auth: AuthApi) = this(ws, auth, "https://api.spotify.com/v1")

  val BASE_URL = baseUrl

  def get[T](endpoint: String)(implicit fmt: Reads[T]): Future[T] = {
    withToken[T](t => validate[T] {
      logResponse {
        ws.url(endpoint)
          .withHeaders(auth.bearer(t.access_token))
          .get()
      }
    }(fmt))
  }

  def getWithOAuth[T](endpoint: String)(implicit fmt: Reads[T]): Future[T] = {
    withAuthToken()(t => validate[T] {
      logResponse {
        ws.url(endpoint)
          .withHeaders(auth.bearer(t.access_token))
          .get()
      }
    }(fmt))
  }

  def getAll[T](call: String => Future[Page[T]])(endpoint: String): Future[List[T]] = {
    def loop(p: Page[T], acc: List[T]): Future[List[T]] = {
      p.next match {
        case null => Future(acc)
        case None => Future(acc)
        case Some(href) =>
          val future: Future[Page[T]] = call(href)
          future flatMap {
            p => loop(p, acc ::: p.items)
          }
      }
    }
    call(endpoint) flatMap {
      p => loop(p, p.items)
    }
  }

  def getAll[T](page: Page[T])(call: String => Future[Page[T]]): Future[List[T]] = {
    def loop(p: Page[T], acc: List[T]): Future[List[T]] = {
      p.next match {
        case None => Future(acc)
        case Some(href) =>
          val future: Future[Page[T]] = call(href)
          future flatMap {
            p => loop(p, acc ::: p.items)
          }
      }
    }
    loop(page, page.items)
  }

  def validate[T](f: Future[WSResponse])(implicit fmt: Reads[T]): Future[T] = {
    f map { response =>
      response.json.validate[T](fmt) match {
        case JsSuccess(obj, _) => obj
        case JsError(_) => throw webApiException(response.json)
      }
    } recoverWith { case ex => Future.failed(ex) }
  }

  private def webApiException(json: JsValue): WebApiException = {
    accessLogger.debug(json.toString)
    json.validate[RegularError] match {
      case JsSuccess(obj, _) => obj
      case JsError(_) => json.validate[AuthError] match {
        case JsSuccess(obj, _) => obj
        case JsError(_) => throw new Exception(s"Unknown exception: ${json.toString}")
      }
    }
  }

  /**
    * Collect disregarding failures
    * @see http://stackoverflow.com/questions/20874186/scala-listfuture-to-futurelist-disregarding-failed-futures
    * @param list
    * @tparam T
    * @return
    */
  def getFutureList[T](list: List[Future[T]]): Future[List[T]] = {
    Future.sequence(
      list.map(futureToFutureTry)
    ).map(_.collect { case Success(x) => x })
  }

  // @see http://stackoverflow.com/a/20874404
  private def futureToFutureTry[T](f: Future[T]): Future[Try[T]] = {
    f.map(Success(_)).recover({case e => Failure(e) })
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

  def callback[T](authCode: String)(request: Token => Future[T]): Future[T] = {
    authorization_code = Some(access(authCode))
    accessLogger.debug(s"authorization_code = ${authorization_code.isDefined}")
    withAuthToken(Some(authCode))(request)
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

