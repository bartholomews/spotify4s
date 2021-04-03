package io.bartholomews.spotify4s.playJson.entities

import io.bartholomews.spotify4s.core.entities.{JsonCodecs, Restrictions, RestrictionsEntitiesSpec}
import io.bartholomews.spotify4s.playJson.PlayEntityCodecs
import play.api.libs.json.{JsValue, Reads, Writes}
import sttp.client3.playJson.SttpPlayJsonApi

class EntitiesCirceSpec
    extends RestrictionsEntitiesSpec[Writes, Reads, JsValue]
    with SttpPlayJsonApi
    with PlayEntityCodecs {
  import io.bartholomews.spotify4s.playJson.codecs._
  override implicit def restrictionsCodecs: JsonCodecs[Restrictions, Writes, Reads, JsValue] =
    entityCodecs[Restrictions]
}
