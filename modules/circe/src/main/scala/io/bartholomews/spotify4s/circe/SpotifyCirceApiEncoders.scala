package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.requests.{
  AddTracksToPlaylistRequest,
  CreatePlaylistRequest,
  ModifyPlaylistRequest
}
import io.circe.Encoder
import io.circe.generic.extras.semiauto.deriveConfiguredEncoder

trait SpotifyCirceApiEncoders {
  import io.bartholomews.spotify4s.circe.codecs.{defaultConfig, dropNullValues}

  implicit val addTracksToPlaylistRequestEncoder: Encoder[AddTracksToPlaylistRequest] =
    dropNullValues(deriveConfiguredEncoder[AddTracksToPlaylistRequest])

  implicit val createPlaylistRequestEncoder: Encoder[CreatePlaylistRequest] =
    dropNullValues(deriveConfiguredEncoder[CreatePlaylistRequest])

  implicit val modifyPlaylistRequestEncoder: Encoder[ModifyPlaylistRequest] =
    dropNullValues(deriveConfiguredEncoder[ModifyPlaylistRequest])
}
