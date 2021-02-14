package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{AudioKey, AudioMode, AudioSection, Confidence, Tempo, TimeSignature}
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath, Reads, Writes}

object AudioSectionPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceCodec
  //noinspection DuplicatedCode
  val reads: Reads[AudioSection] =
    (JsPath \ "start")
      .read[Double]
      .orElse(Reads.pure(0.0))
      .and((JsPath \ "duration").read[Double])
      .and((JsPath \ "confidence").read[Confidence])
      .and((JsPath \ "loudness").read[Double])
      .and(JsPath.read[Tempo](TempoPlayJson.reads))
      .and(JsPath.read[AudioKey](AudioKeyPlayJson.reads))
      .and(JsPath.read[AudioMode](AudioModePlayJson.reads))
      .and(JsPath.read[TimeSignature](TimeSignaturePlayJson.reads))(AudioSection.apply _)

  val writes: Writes[AudioSection] =
    (JsPath \ "start")
      .write[Double]
      .and((JsPath \ "duration").write[Double])
      .and((JsPath \ "confidence").write[Confidence])
      .and((JsPath \ "loudness").write[Double])
      .and(JsPath.write[Tempo](TempoPlayJson.writes))
      .and(JsPath.write[AudioKey](AudioKeyPlayJson.writes))
      .and(JsPath.write[AudioMode](AudioModePlayJson.writes))
      .and(JsPath.write[TimeSignature](TimeSignaturePlayJson.writes))(unlift(AudioSection.unapply))

  val format: Format[AudioSection] = Format(reads, writes)
}
