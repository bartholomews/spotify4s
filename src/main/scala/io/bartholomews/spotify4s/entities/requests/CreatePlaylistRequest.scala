package io.bartholomews.spotify4s.entities.requests

import io.bartholomews.spotify4s.entities.dropNullValues
import io.circe.Encoder
import io.circe.generic.extras.semiauto.deriveConfiguredEncoder

final case class CreatePlaylistRequest(
  name: String,
  public: Boolean,
  collaborative: Boolean,
  description: Option[String]
)

object CreatePlaylistRequest {
  implicit val encoder: Encoder[CreatePlaylistRequest] = dropNullValues(deriveConfiguredEncoder)
}
