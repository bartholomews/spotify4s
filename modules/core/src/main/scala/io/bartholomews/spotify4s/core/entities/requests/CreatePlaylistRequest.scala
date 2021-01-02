package io.bartholomews.spotify4s.core.entities.requests

final case class CreatePlaylistRequest(
  name: String,
  public: Boolean,
  collaborative: Boolean,
  description: Option[String]
)
