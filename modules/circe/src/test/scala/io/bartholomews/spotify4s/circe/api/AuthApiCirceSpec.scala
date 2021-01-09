package io.bartholomews.spotify4s.circe.api

import io.bartholomews.spotify4s.circe.CirceServerBehaviours
import io.bartholomews.spotify4s.core.api.AuthApiSpec
import io.circe.Decoder

class AuthApiCirceSpec extends AuthApiSpec[Decoder, io.circe.Error] with CirceServerBehaviours
