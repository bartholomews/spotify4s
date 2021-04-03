package io.bartholomews.spotify4s.circe.api

import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.spotify4s.circe.{CirceEntityCodecs, CirceServerBehaviours}
import io.bartholomews.spotify4s.core.api.UsersApiSpec
import io.circe

class UsersApiCirceSpec
    extends UsersApiSpec[circe.Encoder, circe.Decoder, circe.Error, circe.Json]
    with WireWordSpec
    with CirceServerBehaviours
    with CirceEntityCodecs
