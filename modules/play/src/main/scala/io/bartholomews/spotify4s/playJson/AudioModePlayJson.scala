package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{AudioMode, Confidence, Modality}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}

object AudioModePlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceDecoder
  val reads: Reads[AudioMode] =
    (JsPath \ "mode")
      .read[Modality](ModalityPlayJson.reads)
      .orElse(Reads.pure(Modality.NoResult))
      .and((JsPath \ "mode_confidence").read[Confidence])(AudioMode.apply _)
}
