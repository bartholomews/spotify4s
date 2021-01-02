package io.bartholomews.spotify4s.core.entities

import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/object-model/#image-object
case class SpotifyImage(height: Option[Int], url: Uri, width: Option[Int])
