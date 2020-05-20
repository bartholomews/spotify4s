package io.bartholomews.spotify4s.entities

import io.circe.generic.extras.ConfiguredJsonCodec

// https://developer.spotify.com/documentation/web-api/reference/object-model/#image-object
@ConfiguredJsonCodec
case class SpotifyImage(height: Option[Int], url: String, width: Option[Int])
