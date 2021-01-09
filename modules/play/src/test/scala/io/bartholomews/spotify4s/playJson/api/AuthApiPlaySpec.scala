package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.spotify4s.core.api.AuthApiSpec
import io.bartholomews.spotify4s.playJson.PlayServerBehaviours
import play.api.libs.json.{JsError, Reads}

class AuthApiPlaySpec extends AuthApiSpec[Reads, JsError] with PlayServerBehaviours
