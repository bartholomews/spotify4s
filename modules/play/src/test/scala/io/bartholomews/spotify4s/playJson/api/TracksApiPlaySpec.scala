package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.spotify4s.core.api.TracksApiSpec
import io.bartholomews.spotify4s.playJson.{PlayEntityCodecs, PlayServerBehaviours}
import play.api.libs.json.{JsError, JsValue, Reads, Writes}

class TracksApiPlaySpec
    extends TracksApiSpec[Writes, Reads, JsError, JsValue]
    with PlayServerBehaviours
    with PlayEntityCodecs
