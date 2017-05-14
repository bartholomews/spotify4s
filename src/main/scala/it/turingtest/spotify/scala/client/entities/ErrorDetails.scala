package it.turingtest.spotify.scala.client.entities

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

/**
  *
  */
sealed trait WebApiException extends Exception {
  self: Throwable =>
  val message: String
  override def getMessage: String = self.message
}

/**
  * @see https://developer.spotify.com/web-api/user-guide/#error-details
  *
  * @param error 	A high level description of the error as specified in RFC 6749 Section 5.2.
  *               @see https://tools.ietf.org/html/rfc6749#section-5.2
  *
  * @param message A more detailed description of the error as specified in RFC 6749 Section 4.1.2.1.
  *                @see https://tools.ietf.org/html/rfc6749#section-4.1.2.1
  */
case class AuthError(error: String, message: String) extends Exception with WebApiException

  object AuthError {
    implicit val authErrorReads: Reads[AuthError] = (
      (JsPath \ "error").read[String] and
        (JsPath \ "error_description").read[String]
      ) (AuthError.apply _)
  }

/**
  * @see https://developer.spotify.com/web-api/object-model/#error-object
  *
  * @param status The HTTP status code (also returned in the response header;
  *               @see https://developer.spotify.com/web-api/user-guide/#response-status-codes).
  *
  * @param message A short description of the cause of the error.
  */
case class RegularError(status: Int, message: String) extends Exception with WebApiException

  object RegularError {
    implicit val errorReads: Reads[RegularError] = (
      (JsPath \ "error" \ "status").read[Int] and
        (JsPath \ "error" \ "message").read[String]
      ) (RegularError.apply _)
  }
