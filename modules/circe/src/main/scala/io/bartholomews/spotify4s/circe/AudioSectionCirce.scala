package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.{
  AudioKey,
  AudioMode,
  AudioSection,
  Confidence,
  Modality,
  PitchClass,
  Tempo,
  TimeSignature
}

import io.circe.{Decoder, HCursor}
import io.circe.Decoder.decodeOption

private[spotify4s] object AudioSectionCirce {
  import codecs._
  import io.bartholomews.spotify4s.circe.codecs.defaultConfig
  val decoder: Decoder[AudioSection] = (c: HCursor) =>
    for {
      start <- c.downField("start").as[Option[Double]]
      duration <- c.downField("duration").as[Double]
      confidence <- c.downField("confidence").as[Confidence]
      loudness <- c.downField("loudness").as[Double]
      tempo <- c.downField("tempo").as[Double]
      tempoConfidence <- c.downField("tempo_confidence").as[Confidence]
      key <- c.downField("key").as[Option[PitchClass]]
      keyConfidence <- c.downField("key_confidence").as[Confidence]
      mode <- c.downField("mode").as[Option[Modality]](decodeOption(ModalityCirce.decoder))
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
