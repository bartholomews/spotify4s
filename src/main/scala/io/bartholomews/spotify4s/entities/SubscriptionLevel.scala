package io.bartholomews.spotify4s.entities

import enumeratum.EnumEntry.Snakecase
import enumeratum._
import io.circe.{Decoder, Encoder, HCursor, Json}

sealed trait SubscriptionLevel extends EnumEntry with Snakecase

case object SubscriptionLevel extends Enum[SubscriptionLevel] {
  case object Premium extends SubscriptionLevel
  case object Free extends SubscriptionLevel
  case class Unknown(value: Option[String]) extends SubscriptionLevel

  override val values: IndexedSeq[SubscriptionLevel] = findValues

  implicit val decoder: Decoder[SubscriptionLevel] = (c: HCursor) =>
    c.value.asString match {
      case Some("premium") => Right(SubscriptionLevel.Premium)
      case Some("free") | Some("open") => Right(SubscriptionLevel.Free)
      case unknown => Right(SubscriptionLevel.Unknown(unknown))
    }

  implicit val encoder: Encoder[SubscriptionLevel] = Encoder.instance {
    case _ @Premium => Json.fromString("premium")
    case _ @Free => Json.fromString("free")
    case _ @Unknown(value) => value.fold(Json.Null)(Json.fromString)
  }
}
