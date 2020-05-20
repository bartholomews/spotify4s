package io.bartholomews.spotify4s.entities

import io.circe.{Decoder, DecodingFailure, HCursor}

/**
  * https://developer.spotify.com/documentation/web-api/#response-schema
  */
sealed trait SpotifyError {
  def message: String
}

object SpotifyError {
  implicit val decoder: Decoder[SpotifyError] =
    Decoder[AuthError]
      .map[SpotifyError](identity)
      .or(Decoder[ApiError].map[SpotifyError](identity))
}

/**
  * Authentication Error Object
  * TODO ADTs
  * @param error  A high level description of the error as specified in RFC 6749 Section 5.2.
  *               (https://tools.ietf.org/html/rfc6749#section-5.2)
  *
  * @param message  A high level description of the error as specified in RFC 6749 Section 5.2.
  *                 (https://tools.ietf.org/html/rfc6749#section-4.1.2.1)
  */
case class AuthError(error: String, message: String) extends SpotifyError
object AuthError {
  implicit val decoder: Decoder[AuthError] = Decoder.forProduct2[AuthError, String, String](
    "error",
    "error_description"
  )((error, message) => AuthError(error, message))
}

/**
  * Regular Error Object
  * @param status  The HTTP status code that is also returned in the response header.
  *                For further information, see:
  *                 https://developer.spotify.com/documentation/web-api/#response-status-codes
  *
  * @param message  A short description of the cause of the error.
  */
case class ApiError(status: Int, message: String) extends SpotifyError

object ApiError {
  import cats.implicits._
  implicit val decoder: Decoder[ApiError] =
    (c: HCursor) =>
      for {
        errorCursor <- Either.fromOption(
                        c.downField("error").success,
                        DecodingFailure("Attempt to decode value on failed cursor: Downfield(error)", c.history)
                      )
        status <- errorCursor.downField("status").as[Int]
        message <- errorCursor.downField("message").as[String]
      } yield ApiError(status, message)
}
