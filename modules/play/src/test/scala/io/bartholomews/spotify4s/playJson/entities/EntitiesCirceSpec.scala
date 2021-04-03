package io.bartholomews.spotify4s.playJson.entities

import io.bartholomews.spotify4s.core.entities.{JsonCodecs, Restrictions, RestrictionsEntitiesSpec}
import play.api.libs.json.{JsValue, Reads, Writes}
import sttp.client3.playJson.SttpPlayJsonApi

class EntitiesCirceSpec
    extends RestrictionsEntitiesSpec[Writes, Reads, JsValue]
    with SttpPlayJsonApi
    with PlayEntityCodecs {
  import io.bartholomews.spotify4s.playJson.codecs._
  override implicit def codecs: JsonCodecs[Restrictions, Writes, Reads, JsValue] = deriveCodecs[Restrictions]
}
