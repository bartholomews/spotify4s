package io.bartholomews.spotify4s.entities

import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder

// https://developer.spotify.com/documentation/general/guides/track-relinking-guide/
case class Restrictions(reason: String)
object Restrictions {
  implicit val decoder: Decoder[Restrictions] = deriveConfiguredDecoder
}
