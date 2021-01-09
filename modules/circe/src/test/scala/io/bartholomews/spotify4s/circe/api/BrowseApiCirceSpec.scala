package io.bartholomews.spotify4s.circe.api

import io.bartholomews.spotify4s.circe.{CirceServerBehaviours, SpotifyCirceApi}
import io.bartholomews.spotify4s.core.api.TracksApiSpec
import io.circe

class BrowseApiCirceSpec
    extends TracksApiSpec[circe.Encoder, circe.Decoder, circe.Error]
    with CirceServerBehaviours
    with SpotifyCirceApi
