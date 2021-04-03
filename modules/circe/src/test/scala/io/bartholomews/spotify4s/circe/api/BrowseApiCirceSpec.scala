package io.bartholomews.spotify4s.circe.api

import io.bartholomews.spotify4s.circe.{CirceEntityCodecs, CirceServerBehaviours, SpotifyCirceApi}
import io.bartholomews.spotify4s.core.api.TracksApiSpec
import io.circe

class BrowseApiCirceSpec
    extends TracksApiSpec[circe.Encoder, circe.Decoder, circe.Error, circe.Json]
    with CirceServerBehaviours
    with CirceEntityCodecs
    with SpotifyCirceApi
