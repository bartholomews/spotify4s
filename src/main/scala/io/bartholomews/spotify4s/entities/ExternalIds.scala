package io.bartholomews.spotify4s.entities

import io.circe._

// https://developer.spotify.com/documentation/web-api/reference-beta/#object-externalidobject
sealed trait ExternalIds {
  def value: String
}

// https://en.wikipedia.org/wiki/International_Standard_Recording_Code
case class ISRC(value: String) extends ExternalIds

// https://en.wikipedia.org/wiki/International_Article_Number
case class EAN(value: String) extends ExternalIds

// https://en.wikipedia.org/wiki/Universal_Product_Code
case class UPC(value: String) extends ExternalIds

object ExternalIds {
  // https://stackoverflow.com/a/57708249
  implicit val decoder: Decoder[ExternalIds] =
    Decoder
      .instance { c =>
        c.value.asObject match {
          case Some(obj) if obj.size == 1 =>
            obj.toIterable.head match {
              case ("isrc", value) => value.as[String].map(ISRC.apply)
              case ("ean", value) => value.as[String].map(EAN.apply)
              case ("upc", value) => value.as[String].map(UPC.apply)
              case (unknown, _) =>
                Left(DecodingFailure(s"ExternalResourceId; unexpected resource standard code: [$unknown]", c.history))
            }

          case None =>
            Left(DecodingFailure("ExternalResourceId; expected singleton object", c.history))
        }
      }

  implicit val encoder: Encoder[ExternalIds] = {
    case ISRC(value) => Json.obj(("isrc", Json.fromString(value)))
    case EAN(value) => Json.obj(("ean", Json.fromString(value)))
    case UPC(value) => Json.obj(("upc", Json.fromString(value)))
  }

  implicit val codec: Codec[ExternalIds] = Codec.from(decoder, encoder)
}
