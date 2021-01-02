package io.bartholomews.spotify4s.core.entities

import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/object-model/#external-url-object
sealed trait ExternalResourceUrl {
  def value: String
}

case class SpotifyResourceUrl(uri: Uri) extends ExternalResourceUrl {
  override val value: String = uri.toString()
}
