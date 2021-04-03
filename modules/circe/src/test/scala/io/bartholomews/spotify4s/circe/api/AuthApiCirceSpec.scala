package io.bartholomews.spotify4s.circe.api

import io.bartholomews.spotify4s.circe.{CirceEntityCodecs, CirceServerBehaviours}
import io.bartholomews.spotify4s.core.api.AuthApiSpec
import io.circe

class AuthApiCirceSpec
    extends AuthApiSpec[circe.Encoder, circe.Decoder, circe.Error, circe.Json]
    with CirceServerBehaviours
    with CirceEntityCodecs
