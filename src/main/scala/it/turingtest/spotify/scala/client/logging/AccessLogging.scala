package it.turingtest.spotify.scala.client.logging

import play.api.Logger
import play.api.libs.ws.WSResponse
import play.api.mvc.Result

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * https://www.playframework.com/documentation/2.5.x/ScalaLogging
  */
trait AccessLogging {

  // @see https://www.playframework.com/documentation/2.5.x/SettingsLogger
  val accessLogger = Logger("spotify-scala-client")

  def withLogger(call: Future[WSResponse])(action: WSResponse => Result): Future[Result] = {
    call map { response: WSResponse => { accessLogger.debug(response.body); action(response) } }
  }

  def logResponse(call: Future[WSResponse]): Future[WSResponse] = {
    call map { response: WSResponse =>
      accessLogger.info(s"${response.status}, ${response.statusText}")
      accessLogger.debug(response.body)
      response
    }
  }

}
