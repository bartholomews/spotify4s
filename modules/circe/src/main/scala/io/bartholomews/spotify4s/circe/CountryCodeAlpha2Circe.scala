package io.bartholomews.spotify4s.circe

import io.bartholomews.iso.CountryCodeAlpha2
import io.circe.{Codec, Decoder, Encoder, Json}

private[spotify4s] object CountryCodeAlpha2Circe {
  val encoder: Encoder[CountryCodeAlpha2] = cc => Json.fromString(cc.value)
  val decoder: Decoder[CountryCodeAlpha2] = Decoder.decodeString.emap(
    str => CountryCodeAlpha2.values.find(_.value == str).toRight(s"Invalid ISO_3166-1 code: [$str]")
  )

  val codec: Codec[CountryCodeAlpha2] = Codec.from(decoder, encoder)
}
