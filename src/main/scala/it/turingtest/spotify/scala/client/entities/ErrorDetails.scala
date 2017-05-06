package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

/**
  * @see https://developer.spotify.com/web-api/user-guide/#error-details
  */
sealed trait WebApiException extends Exception {
  self: Throwable =>
  val message: String
  override def getMessage: String = self.message
}

case class AuthError(error: String, message: String) extends Exception with WebApiException

  object AuthError {
    implicit val authErrorReads: Reads[AuthError] = (
      (JsPath \ "error").read[String] and
        (JsPath \ "error_description").read[String]
      ) (AuthError.apply _)
  }

case class RegularError(status: Int, message: String) extends Exception with WebApiException

  object RegularError {
    implicit val errorReads: Reads[RegularError] = (
      (JsPath \ "error" \ "status").read[Int] and
        (JsPath \ "error" \ "message").read[String]
      ) (RegularError.apply _)
  }
