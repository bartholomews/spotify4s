package io.bartholomews.spotify4s.entities

import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/object-model/#image-object
case class SpotifyImage(height: Option[Int], url: Uri, width: Option[Int])
object SpotifyImage {
  implicit val decoder: Decoder[SpotifyImage] = deriveConfiguredDecoder
}
