package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.spotify4s.core.api.TracksApiSpec
import io.bartholomews.spotify4s.playJson.PlayServerBehaviours
import play.api.libs.json.{JsError, Reads}

class TracksApiPlaySpec extends TracksApiSpec[Reads, JsError] with PlayServerBehaviours
