package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.SubscriptionLevel
import io.bartholomews.spotify4s.core.entities.SubscriptionLevel.{Free, Premium, Unknown}
import io.circe.{Codec, Decoder, Encoder, HCursor, Json}

private[spotify4s] object SubscriptionLevelCirce {
  val decoder: Decoder[SubscriptionLevel] = (c: HCursor) =>
    c.value.asString match {
      case Some("premium") => Right(SubscriptionLevel.Premium)
      case Some("free") | Some("open") => Right(SubscriptionLevel.Free)
      case unknown => Right(SubscriptionLevel.Unknown(unknown))
    }

  val encoder: Encoder[SubscriptionLevel] = Encoder.instance {
    case _ @Premium => Json.fromString("premium")
    case _ @Free => Json.fromString("free")
    case _ @Unknown(value) => value.fold(Json.Null)(Json.fromString)
  }

  val codec: Codec[SubscriptionLevel] = Codec.from(decoder, encoder)
}
