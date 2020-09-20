package io.bartholomews.spotify4s.entities

import io.circe.{Decoder, HCursor}

// https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-analysis/#section-object
case class AudioSection(
  start: Double,
  duration: Double,
  confidence: Confidence,
  loudness: Double,
  tempo: Tempo,
  key: AudioKey,
  mode: AudioMode,
  timeSignature: TimeSignature
)

object AudioSection {
  implicit val decoder: Decoder[AudioSection] = (c: HCursor) =>
    for {
      start <- c.downField("start").as[Option[Double]]
      duration <- c.downField("duration").as[Double]
      confidence <- c.downField("confidence").as[Confidence]
      loudness <- c.downField("loudness").as[Double]
      tempo <- c.downField("tempo").as[Double]
      tempoConfidence <- c.downField("tempo_confidence").as[Confidence]
      key <- c.downField("key").as[Option[PitchClass]]
      keyConfidence <- c.downField("key_confidence").as[Confidence]
      mode <- c.downField("mode").as[Option[Modality]]
      modeConfidence <- c.downField("mode_confidence").as[Confidence]
      timeSignature <- c.downField("time_signature").as[Int]
      timeSignatureConfidence <- c.downField("time_signature_confidence").as[Confidence]
    } yield AudioSection(
      start.getOrElse(0.0),
      duration,
      confidence,
      loudness,
      Tempo(tempo, tempoConfidence),
      AudioKey(key, keyConfidence),
      AudioMode(mode.getOrElse(Modality.NoResult), modeConfidence),
      TimeSignature(timeSignature, timeSignatureConfidence)
    )
}

case class Tempo(value: Double, confidence: Confidence)

case class AudioKey(value: Option[PitchClass], confidence: Confidence)

case class AudioMode(value: Modality, confidence: Confidence)

case class TimeSignature(value: Double, confidence: Confidence)
