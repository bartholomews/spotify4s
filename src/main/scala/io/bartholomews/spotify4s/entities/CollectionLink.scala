package io.bartholomews.spotify4s.entities

import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder

case class CollectionLink(
  href: SpotifyUri,
  total: Int
)

object CollectionLink {
  implicit val decoder: Decoder[CollectionLink] = deriveConfiguredDecoder
}
