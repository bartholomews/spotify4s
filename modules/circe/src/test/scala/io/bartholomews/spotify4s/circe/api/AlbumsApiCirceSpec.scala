package io.bartholomews.spotify4s.circe.api

import io.bartholomews.spotify4s.circe.{CirceServerBehaviours, SpotifyCirceApi}
import io.bartholomews.spotify4s.core.api.AlbumsApiSpec
import io.circe

class AlbumsApiCirceSpec
    extends AlbumsApiSpec[circe.Encoder, circe.Decoder, circe.Error]
    with CirceServerBehaviours
    with SpotifyCirceApi
