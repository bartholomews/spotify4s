package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{AudioMode, Confidence, Modality}
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath, Reads, Writes}

object AudioModePlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceCodec
  val reads: Reads[AudioMode] =
    (JsPath \ "mode")
      .read[Modality](ModalityPlayJson.reads)
      .orElse(Reads.pure(Modality.NoResult))
      .and((JsPath \ "mode_confidence").read[Confidence])(AudioMode.apply _)

  val writes: Writes[AudioMode] =
    (JsPath \ "mode")
      .write[Modality](ModalityPlayJson.writes)
      .and((JsPath \ "mode_confidence").write[Confidence])(unlift(AudioMode.unapply))

  val codec: Format[AudioMode] = Format(reads, writes)
}
