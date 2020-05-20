package io.bartholomews.spotify4s.entities

import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.{Decoder, HCursor}

@ConfiguredJsonCodec(encodeOnly = true)
case class AudioSegment(
  start: Double,
  duration: Double,
  confidence: Confidence,
  loudness: Loudness,
  pitches: List[Double], // Todo 0 to 12 Float?
  timbre: List[Double]
)

object AudioSegment {
  implicit val decoder: Decoder[AudioSegment] = (c: HCursor) =>
    for {
      start <- c.downField("start").as[Option[Double]]
      duration <- c.downField("duration").as[Double]
      confidence <- c.downField("confidence").as[Confidence]
      loudnessStart <- c.downField("loudness_start").as[Double]
      loudnessMax <- c.downField("loudness_max").as[Double]
      loudnessMaxTime <- c.downField("loudness_max_time").as[Double]
      loudnessEnd <- c.downField("loudness_end").as[Option[Double]]
      pitches <- c.downField("pitches").as[List[Double]]
      timbre <- c.downField("timbre").as[List[Double]]
    } yield
      AudioSegment(
        start.getOrElse(0.0),
        duration,
        confidence,
        Loudness(loudnessStart, loudnessMax, loudnessMaxTime, loudnessEnd),
        pitches,
        timbre
    )
}

@ConfiguredJsonCodec(encodeOnly = true)
case class Loudness(
  start: Double,
  max: Double,
  maxTime: Double,
  end: Option[Double]
)
