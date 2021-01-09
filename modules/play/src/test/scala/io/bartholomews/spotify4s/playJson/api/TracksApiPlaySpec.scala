package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.spotify4s.core.api.TracksApiSpec
import io.bartholomews.spotify4s.playJson.PlayServerBehaviours
import play.api.libs.json.{JsError, Reads, Writes}

class TracksApiPlaySpec extends TracksApiSpec[Writes, Reads, JsError] with PlayServerBehaviours
