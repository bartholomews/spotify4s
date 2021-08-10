package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities._
import io.circe.Decoder.decodeOption
import io.circe.syntax.EncoderOps
import io.circe._

private[spotify4s] object AudioSectionCirce {
  import codecs._
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

  val encoder: Encoder[AudioSection] = (a: AudioSection) =>
    Json.obj(
      ("start", a.start.asJson),
      ("duration", a.duration.asJson),
      ("confidence", a.confidence.asJson),
      ("loudness", a.loudness.asJson),
      ("tempo", a.tempo.value.asJson),
      ("tempo_confidence", a.tempo.confidence.asJson),
      ("key", a.key.value.asJson),
      ("key_confidence", a.key.confidence.asJson),
      ("mode", a.mode.value.asJson),
      ("mode_confidence", a.mode.confidence.asJson),
      ("time_signature", a.timeSignature.value.asJson),
      ("time_signature_confidence", a.timeSignature.confidence.asJson)
    )

  val codec: Codec[AudioSection] = Codec.from(decoder, encoder)
}
