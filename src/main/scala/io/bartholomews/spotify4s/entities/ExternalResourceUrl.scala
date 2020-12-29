package io.bartholomews.spotify4s.entities

import io.circe._
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/object-model/#external-url-object
sealed trait ExternalResourceUrl {
  def value: String
}

case class SpotifyResourceUrl(uri: Uri) extends ExternalResourceUrl {
  override val value: String = uri.toString()
}

object ExternalResourceUrl {
  // https://stackoverflow.com/a/57708249
  implicit val decoder: Decoder[ExternalResourceUrl] =
    Decoder
      .instance { c =>
        c.value.asObject match {
          case Some(obj) if obj.size == 1 =>
            obj.toIterable.head match {
              case ("spotify", value) => value.as[Uri].map(SpotifyResourceUrl.apply)
              case (unknown, _) =>
                Left(DecodingFailure(s"SpotifyResourceUrl; unexpected resource url type: [$unknown]", c.history))
            }

          case _ =>
            Left(DecodingFailure("ExternalResourceUrl; expected singleton object", c.history))
        }
      }

  implicit val encoder: Encoder[ExternalResourceUrl] = {
    case s: SpotifyResourceUrl => Json.obj(("spotify", Json.fromString(s.value)))
  }

  implicit val codec: Codec[ExternalResourceUrl] = Codec.from(decoder, encoder)
}
