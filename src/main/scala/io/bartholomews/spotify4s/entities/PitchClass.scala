package io.bartholomews.spotify4s.entities

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec

/**
  * https://en.wikipedia.org/wiki/Pitch_class
  * TODO -1 to 11 ?
  *
  * @param value Integers map to pitches using standard Pitch Class notation.
  *              E.g. 0 = C, 1 = C♯/D♭, 2 = D, and so on. If no key was detected, the value is -1.
  */
case class PitchClass(value: Int) extends AnyVal
object PitchClass {
  implicit val codec: Codec[PitchClass] = deriveUnwrappedCodec
}
