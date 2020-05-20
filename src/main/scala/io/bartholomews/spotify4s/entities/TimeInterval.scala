package io.bartholomews.spotify4s.entities

import io.circe.Decoder
import io.circe.generic.extras.ConfiguredJsonCodec

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

@ConfiguredJsonCodec(encodeOnly = true)
case class Bar(start: Double, duration: Double, confidence: Confidence) extends TimeInterval
object Bar {
  implicit val decoder: Decoder[Bar] = TimeInterval.mkDecoder(Bar.apply)
}

@ConfiguredJsonCodec(encodeOnly = true)
case class Beat(start: Double, duration: Double, confidence: Confidence) extends TimeInterval
object Beat {
  implicit val decoder: Decoder[Beat] = TimeInterval.mkDecoder(Beat.apply)
}

@ConfiguredJsonCodec(encodeOnly = true)
case class Tatum(start: Double, duration: Double, confidence: Confidence) extends TimeInterval
object Tatum {
  implicit val decoder: Decoder[Tatum] = TimeInterval.mkDecoder(Tatum.apply)
}
