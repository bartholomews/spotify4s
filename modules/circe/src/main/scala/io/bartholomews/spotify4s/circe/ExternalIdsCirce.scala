package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.ExternalIds
import io.bartholomews.spotify4s.core.entities.ExternalIds._
import io.circe.{Codec, Decoder, DecodingFailure, Encoder, Json}

private[spotify4s] object ExternalIdsCirce {
  // https://stackoverflow.com/a/57708249
  val decoder: Decoder[ExternalIds] =
    Decoder
      .instance { c =>
        c.value.asObject match {
          case Some(obj) if obj.size == 1 =>
            obj.toIterable.head match {
              case ("isrc", value) => value.as[String].map(ISRC.apply)
              case ("ean", value) => value.as[String].map(EAN.apply)
              case ("upc", value) => value.as[String].map(UPC.apply)
              case (unknown, _) =>
                Left(DecodingFailure(s"ExternalId; unexpected resource standard code: [$unknown]", c.history))
            }

          case _ =>
            Left(DecodingFailure("ExternalId; expected singleton object", c.history))
        }
      }

  val encoder: Encoder[ExternalIds] = {
    case ISRC(value) => Json.obj(("isrc", Json.fromString(value)))
    case EAN(value) => Json.obj(("ean", Json.fromString(value)))
    case UPC(value) => Json.obj(("upc", Json.fromString(value)))
  }

  val codec: Codec[ExternalIds] = Codec.from(decoder, encoder)
}
