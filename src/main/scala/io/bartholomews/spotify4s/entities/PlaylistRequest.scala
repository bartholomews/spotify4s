package io.bartholomews.spotify4s.entities

import io.bartholomews.fsclient.codecs.FsJsonRequest
import io.circe.Encoder
import io.circe.generic.extras.semiauto.deriveConfiguredEncoder

final case class PlaylistRequest(
  name: String,
  public: Boolean,
  collaborative: Boolean,
  description: Option[String]
)

object PlaylistRequest extends FsJsonRequest[PlaylistRequest] {
  implicit val encoder: Encoder[PlaylistRequest] = deriveConfiguredEncoder
}
