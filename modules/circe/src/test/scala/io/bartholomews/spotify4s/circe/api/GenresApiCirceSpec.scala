package io.bartholomews.spotify4s.circe.api

import io.bartholomews.spotify4s.circe.{CirceEntityCodecs, CirceServerBehaviours, SpotifyCirceApi}
import io.bartholomews.spotify4s.core.api.{CategoriesApiSpec, GenresApiSpec}
import io.circe

class GenresApiCirceSpec
    extends GenresApiSpec[circe.Encoder, circe.Decoder, circe.Error, circe.Json]
    with CirceServerBehaviours
    with CirceEntityCodecs
    with SpotifyCirceApi
