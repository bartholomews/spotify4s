package io.bartholomews.spotify4s.entities

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec

// TODO value should be Refined 0.0 to 1.0
case class Confidence(value: Double) extends AnyVal

object Confidence {
  implicit val codec: Codec[Confidence] = deriveUnwrappedCodec
}
