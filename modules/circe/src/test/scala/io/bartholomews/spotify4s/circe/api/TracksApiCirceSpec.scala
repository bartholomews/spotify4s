package io.bartholomews.spotify4s.circe.api

import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.spotify4s.circe.CirceServerBehaviours
import io.bartholomews.spotify4s.core.api.TracksApiSpec
import io.circe

class TracksApiCirceSpec
    extends TracksApiSpec[circe.Encoder, circe.Decoder, circe.Error]
    with WireWordSpec
    with CirceServerBehaviours
