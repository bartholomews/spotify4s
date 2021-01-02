package io.bartholomews.spotify4s.core.entities

/**
  * https://developer.spotify.com/documentation/web-api/#response-schema
  */
sealed trait SpotifyError {
  def message: String
}

/**
  * Authentication Error Object
  * @param error  A high level description of the error as specified in RFC 6749 Section 5.2.
  *               (https://tools.ietf.org/html/rfc6749#section-5.2)
  *
  * @param message  A high level description of the error as specified in RFC 6749 Section 5.2.
  *                 (https://tools.ietf.org/html/rfc6749#section-4.1.2.1)
  */
case class AuthError(error: String, message: String) extends SpotifyError

/**
  * Regular Error Object
  * @param status  The HTTP status code that is also returned in the response header.
  *                For further information, see:
  *                 https://developer.spotify.com/documentation/web-api/#response-status-codes
  *
  * @param message  A short description of the cause of the error.
  */
case class ApiError(status: Int, message: String) extends SpotifyError
