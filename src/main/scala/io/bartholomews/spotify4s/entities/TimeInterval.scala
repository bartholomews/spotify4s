package io.bartholomews.spotify4s.entities

import io.circe.{Codec, Decoder}
import io.circe.generic.extras.semiauto.deriveConfiguredEncoder

// https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-analysis/#time-interval-object
sealed trait TimeInterval {
  def start: Double
  def duration: Double
  def confidence: Confidence
}

object TimeInterval {
  def mkDecoder[A <: TimeInterval](f: (Double, Double, Confidence) => A): Decoder[A] =
    Decoder.forProduct3[A, Double, Double, Option[Double]]("start", "duration", "confidence")({
      case (start, duration, maybeConfidence) => f(start, duration, Confidence(maybeConfidence.getOrElse(0.0)))
    })
}

case class Bar(start: Double, duration: Double, confidence: Confidence) extends TimeInterval
object Bar {
  implicit val codec: Codec[Bar] = Codec.from(TimeInterval.mkDecoder(Bar.apply), deriveConfiguredEncoder)
}

case class Beat(start: Double, duration: Double, confidence: Confidence) extends TimeInterval
object Beat {
  implicit val codec: Codec[Beat] = Codec.from(TimeInterval.mkDecoder(Beat.apply), deriveConfiguredEncoder)
}

case class Tatum(start: Double, duration: Double, confidence: Confidence) extends TimeInterval
object Tatum {
  implicit val codec: Codec[Tatum] = Codec.from(TimeInterval.mkDecoder(Tatum.apply), deriveConfiguredEncoder)
}
