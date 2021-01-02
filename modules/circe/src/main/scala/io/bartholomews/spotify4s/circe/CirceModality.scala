package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.Modality
import io.circe.{Codec, Decoder, Encoder}

object CirceModality {
  val encoder: Encoder[Modality] = Encoder.encodeInt.contramap(_.value)
  val decoder: Decoder[Modality] = Decoder.decodeInt.emap(
    intValue =>
      Modality.values
        .find(_.value == intValue)
        .toRight(s"[$intValue] is not a valid Modality value")
  )
  val codec: Codec[Modality] = Codec.from(decoder, encoder)
}
