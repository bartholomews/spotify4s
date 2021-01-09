package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.spotify4s.core.api.UsersApiSpec
import io.bartholomews.spotify4s.playJson.PlayServerBehaviours
import play.api.libs.json.{JsError, Reads, Writes}

class UsersApiPlaySpec extends UsersApiSpec[Writes, Reads, JsError] with WireWordSpec with PlayServerBehaviours