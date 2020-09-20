package io.bartholomews.spotify4s.entities.requests

import io.bartholomews.fsclient.codecs.FsJsonRequest
import io.bartholomews.spotify4s.entities.dropNullValues
import io.circe.Encoder
import io.circe.generic.extras.semiauto.deriveConfiguredEncoder

final case class ModifyPlaylistRequest(
  name: Option[String],
  public: Option[Boolean],
  collaborative: Option[Boolean],
  description: Option[String]
)

object ModifyPlaylistRequest extends FsJsonRequest[ModifyPlaylistRequest] {
  implicit val encoder: Encoder[ModifyPlaylistRequest] = dropNullValues(deriveConfiguredEncoder)
}
