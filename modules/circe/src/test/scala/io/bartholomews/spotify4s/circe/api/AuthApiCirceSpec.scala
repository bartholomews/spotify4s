package io.bartholomews.spotify4s.circe.api

import io.bartholomews.spotify4s.circe.CirceServerBehaviours
import io.bartholomews.spotify4s.core.api.AuthApiSpec

class AuthApiCirceSpec
    extends AuthApiSpec[io.circe.Encoder, io.circe.Decoder, io.circe.Error]
    with CirceServerBehaviours
