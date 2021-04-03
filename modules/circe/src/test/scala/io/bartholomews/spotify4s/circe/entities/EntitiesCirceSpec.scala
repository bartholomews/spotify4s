package io.bartholomews.spotify4s.circe.entities

import io.bartholomews.spotify4s.core.entities.{JsonCodecs, Restrictions, RestrictionsEntitiesSpec}
import io.circe.{Decoder, Encoder, Json}
import sttp.client3.circe.SttpCirceApi

class EntitiesCirceSpec
    extends RestrictionsEntitiesSpec[Encoder, Decoder, Json]
    with SttpCirceApi
    with CirceEntityCodecs {
  import io.bartholomews.spotify4s.circe.codecs._
  override implicit def codecs: JsonCodecs[Restrictions, Encoder, Decoder, Json] = deriveCodecs
}