package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.{AudioSegment, Confidence, Loudness}
import io.circe.{Decoder, HCursor}

private[spotify4s] object AudioSegmentCirce {
  import codecs._
  val decoder: Decoder[AudioSegment] = (c: HCursor) =>
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
    } yield AudioSegment(
      start.getOrElse(0.0),
      duration,
      confidence,
      Loudness(loudnessStart, loudnessMax, loudnessMaxTime, loudnessEnd),
      pitches,
      timbre
    )
}
