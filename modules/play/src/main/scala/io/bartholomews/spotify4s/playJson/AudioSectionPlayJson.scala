package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{AudioKey, AudioMode, AudioSection, Confidence, Tempo, TimeSignature}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}

object AudioSectionPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceDecoder
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
}
