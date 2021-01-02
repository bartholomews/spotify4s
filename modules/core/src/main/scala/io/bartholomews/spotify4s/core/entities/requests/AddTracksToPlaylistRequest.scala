package io.bartholomews.spotify4s.core.entities.requests

case class AddTracksToPlaylistRequest(uris: List[String], position: Option[Int])
