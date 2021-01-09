package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.spotify4s.core.api.BrowseApiSpec
import io.bartholomews.spotify4s.playJson.PlayServerBehaviours
import play.api.libs.json.{JsError, Reads}

class BrowseApiPlaySpec extends BrowseApiSpec[Reads, JsError] with PlayServerBehaviours
