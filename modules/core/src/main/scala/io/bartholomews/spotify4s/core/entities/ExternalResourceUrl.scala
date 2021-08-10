package io.bartholomews.spotify4s.core.entities

import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/object-model/#external-url-object
sealed trait ExternalResourceUrl

object ExternalResourceUrl {
  final case object Empty extends ExternalResourceUrl
  final case class SpotifyResourceUrl(uri: Uri) extends ExternalResourceUrl
}
