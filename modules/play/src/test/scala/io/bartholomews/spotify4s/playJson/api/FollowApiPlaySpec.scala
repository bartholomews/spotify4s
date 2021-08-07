package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.spotify4s.core.api.FollowApiSpec
import io.bartholomews.spotify4s.playJson.{PlayEntityCodecs, PlayServerBehaviours}
import play.api.libs.json.{JsError, JsValue, Reads, Writes}

class FollowApiPlaySpec
    extends FollowApiSpec[Writes, Reads, JsError, JsValue]
    with PlayServerBehaviours
    with PlayEntityCodecs
