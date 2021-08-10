package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.{AudioSegment, Confidence, Loudness}
import io.circe.syntax.EncoderOps
import io.circe.{Codec, Decoder, Encoder, HCursor, Json}

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

  val encoder: Encoder[AudioSegment] = (a: AudioSegment) => Json.obj(
    ("start", a.start.asJson),
    ("duration", a.duration.asJson),
    ("confidence", a.confidence.asJson),
    ("loudness_start", a.loudness.start.asJson),
    ("loudness_max", a.loudness.max.asJson),
    ("loudness_max_time", a.loudness.maxTime.asJson),
    ("loudness_end", a.loudness.end.asJson),
    ("pitches", a.pitches.asJson),
    ("timbre", a.timbre.asJson),
  )

  val codec: Codec[AudioSegment] = Codec.from(decoder, encoder)
}
