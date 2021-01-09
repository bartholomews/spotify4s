package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{Confidence, Tempo}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}

object TempoPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceDecoder
  val reads: Reads[Tempo] =
    (JsPath \ "tempo")
      .read[Double]
      .and((JsPath \ "tempo_confidence").read[Confidence])(Tempo.apply _)
}
