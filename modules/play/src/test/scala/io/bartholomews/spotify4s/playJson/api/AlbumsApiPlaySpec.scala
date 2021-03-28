package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.spotify4s.core.api.AlbumsApiSpec
import io.bartholomews.spotify4s.playJson.PlayServerBehaviours
import play.api.libs.json.{JsError, Reads, Writes}

class AlbumsApiPlaySpec extends AlbumsApiSpec[Writes, Reads, JsError] with PlayServerBehaviours
