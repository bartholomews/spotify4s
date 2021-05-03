package io.bartholomews.spotify4s.circe.entities

import io.bartholomews.scalatestudo.entities.JsonCodecs
import io.bartholomews.spotify4s.circe.CirceEntityCodecs
import io.bartholomews.spotify4s.core.entities.{Restrictions, RestrictionsSpec}
import io.circe.{Decoder, Encoder, Json}
import sttp.client3.circe.SttpCirceApi

class EntitiesCirceSpec extends RestrictionsSpec[Encoder, Decoder, Json] with SttpCirceApi with CirceEntityCodecs {
  import io.bartholomews.spotify4s.circe.codecs._
  override implicit def restrictionsCodecs: JsonCodecs[Restrictions, Encoder, Decoder, Json] = entityCodecs
}
