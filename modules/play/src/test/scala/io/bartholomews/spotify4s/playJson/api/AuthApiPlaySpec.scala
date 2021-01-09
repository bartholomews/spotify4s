package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.spotify4s.core.api.AuthApiSpec
import io.bartholomews.spotify4s.playJson.PlayServerBehaviours
import play.api.libs.json.{JsError, Reads, Writes}

class AuthApiPlaySpec extends AuthApiSpec[Writes, Reads, JsError] with PlayServerBehaviours
