package io.bartholomews.spotify4s.entities

import io.bartholomews.fsclient.codecs.FsJsonRequest
import io.circe.Encoder
import io.circe.generic.extras.semiauto.deriveConfiguredEncoder

final case class CreatePlaylistRequest(
  name: String,
  public: Boolean,
  collaborative: Boolean,
  description: Option[String]
)

object CreatePlaylistRequest extends FsJsonRequest[CreatePlaylistRequest] {
  implicit val encoder: Encoder[CreatePlaylistRequest] = deriveConfiguredEncoder
}

final case class ModifyPlaylistRequest(
  name: Option[String],
  public: Option[Boolean],
  collaborative: Option[Boolean],
  description: Option[String]
)

object ModifyPlaylistRequest extends FsJsonRequest[ModifyPlaylistRequest] {
  implicit val encoder: Encoder[ModifyPlaylistRequest] = deriveConfiguredEncoder
}
