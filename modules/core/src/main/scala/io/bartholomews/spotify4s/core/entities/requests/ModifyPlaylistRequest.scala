package io.bartholomews.spotify4s.core.entities.requests

final case class ModifyPlaylistRequest(
  name: Option[String],
  public: Option[Boolean],
  collaborative: Option[Boolean],
  description: Option[String]
)
