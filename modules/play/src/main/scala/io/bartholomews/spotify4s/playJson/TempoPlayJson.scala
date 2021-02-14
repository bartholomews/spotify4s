package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{Confidence, Tempo}
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath, Reads, Writes}

object TempoPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceCodec
  val reads: Reads[Tempo] =
    (JsPath \ "tempo")
      .read[Double]
      .and((JsPath \ "tempo_confidence").read[Confidence])(Tempo.apply _)

  val writes: Writes[Tempo] =
    (JsPath \ "tempo")
      .write[Double]
      .and((JsPath \ "tempo_confidence").write[Confidence])(unlift(Tempo.unapply))

  val format: Format[Tempo] = Format(reads, writes)
}
