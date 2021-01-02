package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.{Confidence, TimeInterval}
import io.bartholomews.spotify4s.core.entities.TimeInterval.{Bar, Beat, Tatum}
import io.circe.{Codec, Decoder}
import io.circe.generic.extras.semiauto.deriveConfiguredEncoder

object CirceTimeInterval {
  private def timeIntervalDecoderF[A <: TimeInterval](f: (Double, Double, Confidence) => A): Decoder[A] =
    Decoder.forProduct3[A, Double, Double, Option[Double]]("start", "duration", "confidence")({
      case (start, duration, maybeConfidence) => f(start, duration, Confidence(maybeConfidence.getOrElse(0.0)))
    })

  val barCodec: Codec[Bar] = Codec.from(timeIntervalDecoderF(Bar.apply), deriveConfiguredEncoder)
  val beatCodec: Codec[Beat] = Codec.from(timeIntervalDecoderF(Beat.apply), deriveConfiguredEncoder)
  val tatumCodec: Codec[Tatum] = Codec.from(timeIntervalDecoderF(Tatum.apply), deriveConfiguredEncoder)
}
