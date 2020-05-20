package io.bartholomews.spotify4s.entities

import io.circe._

// https://developer.spotify.com/documentation/web-api/reference/object-model/#external-url-object
sealed trait ExternalResourceUrl {
  def value: String
}

case class SpotifyResourceUrl(value: String) extends ExternalResourceUrl

object ExternalResourceUrl {
  // https://stackoverflow.com/a/57708249
  implicit val decoder: Decoder[ExternalResourceUrl] =
    Decoder
      .instance { c =>
        c.value.asObject match {
          case Some(obj) if obj.size == 1 =>
            obj.toIterable.head match {
              case ("spotify", value) => value.as[String].map(SpotifyResourceUrl.apply)
              case (unknown, _) =>
                Left(DecodingFailure(s"SpotifyResourceUrl; unexpected resource url type: [$unknown]", c.history))
            }

          case _ =>
            Left(DecodingFailure("ExternalResourceUrl; expected singleton object", c.history))
        }
      }

  implicit val encoder: Encoder[ExternalResourceUrl] = {
    case SpotifyResourceUrl(value) => Json.obj(("spotify", Json.fromString(value)))
  }

  implicit val codec: Codec[ExternalResourceUrl] = Codec.from(decoder, encoder)
}
