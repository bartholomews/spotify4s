package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.{ApiError, AuthError, SpotifyError}
import io.circe.{Decoder, DecodingFailure, HCursor}

object CirceSpotifyError {
  import cats.implicits._

  private val authErrorDecoder: Decoder[AuthError] = Decoder.forProduct2[AuthError, String, String](
    "error",
    "error_description"
  )((error, message) => AuthError(error, message))

  private val apiErrorDecoder: Decoder[ApiError] =
    (c: HCursor) =>
      for {
        errorCursor <- Either.fromOption(
                        c.downField("error").success,
                        DecodingFailure("Attempt to decode value on failed cursor: Downfield(error)", c.history)
                      )
        status <- errorCursor.downField("status").as[Int]
        message <- errorCursor.downField("message").as[String]
      } yield ApiError(status, message)

  val spotifyErrorDecoder: Decoder[SpotifyError] =
    authErrorDecoder
      .map[SpotifyError](identity)
      .or(apiErrorDecoder.map[SpotifyError](identity))
}
