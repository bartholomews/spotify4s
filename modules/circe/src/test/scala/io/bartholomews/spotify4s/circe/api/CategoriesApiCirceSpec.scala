package io.bartholomews.spotify4s.circe.api

import io.bartholomews.spotify4s.circe.{CirceEntityCodecs, CirceServerBehaviours, SpotifyCirceApi}
import io.bartholomews.spotify4s.core.api.CategoriesApiSpec
import io.circe

class CategoriesApiCirceSpec
    extends CategoriesApiSpec[circe.Encoder, circe.Decoder, circe.Error, circe.Json]
    with CirceServerBehaviours
    with CirceEntityCodecs
    with SpotifyCirceApi
