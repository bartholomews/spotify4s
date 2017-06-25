package it.turingtest.spotify.scala.client.entities

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

/**
  * @see https://web.archive.org/web/20160528174915/http://developer.echonest.com/docs/v4/_static/AnalyzeDocumentation.pdf
  */
case class AudioAnalysis(bars: Seq[TimeUnit], beats: Seq[TimeUnit], metaAnalysis: MetaAnalysis, sections: Seq[Section],
                         segments: Seq[Segment], tatums: Seq[TimeUnit], trackAnalysis: TrackAnalysis)

object AudioAnalysis {
  implicit val audioAnalysisReads: Reads[AudioAnalysis] = (
    (JsPath \ "bars").read[Seq[TimeUnit]] and
      (JsPath \ "beats").read[Seq[TimeUnit]] and
      (JsPath \ "meta").read[MetaAnalysis] and
      (JsPath \ "sections").read[Seq[Section]] and
      (JsPath \ "segments").read[Seq[Segment]] and
      (JsPath \ "tatums").read[Seq[TimeUnit]] and
      (JsPath \ "track").read[TrackAnalysis]
    ) (AudioAnalysis.apply _)
}
// =====================================================================================================================

case class TimeUnit(start: Float, duration: Float, confidence: Float)

object TimeUnit {
  implicit val timeUnitReads: Reads[TimeUnit] = (
    (JsPath \ "start").read[Float] and
      (JsPath \ "duration").read[Float] and
      (JsPath \ "confidence").read[Float]
    ) (TimeUnit.apply _)
}

// =====================================================================================================================

case class Section(start: Float, duration: Float, confidence: Float, loudness: Float, tempo: Float,
                   tempo_confidence: Float, key: Int, key_confidence: Float, mode: Int, mode_confidence: Float,
                   time_signature: Int, time_signature_confidence: Float)

object Section {
  implicit val sectionReads: Reads[Section] = (
    (JsPath \ "start").read[Float] and
      (JsPath \ "duration").read[Float] and
      (JsPath \ "confidence").read[Float] and
      (JsPath \ "loudness").read[Float] and
      (JsPath \ "tempo").read[Float] and
      (JsPath \ "tempo_confidence").read[Float] and
      (JsPath \ "key").read[Int] and
      (JsPath \ "key_confidence").read[Float] and
      (JsPath \ "mode").read[Int] and
      (JsPath \ "mode_confidence").read[Float] and
      (JsPath \ "time_signature").read[Int] and
      (JsPath \ "time_signature_confidence").read[Float]
    ) (Section.apply _)
}

// =====================================================================================================================

case class Segment(start: Float, duration: Float, confidence: Float, loudness_start: Float, loudness_max_time: Float,
                   loudness_max: Float, loudness_end: Option[Float], pitches: Seq[Float], timbre: Seq[Float])

object Segment {
  implicit val segmentReads: Reads[Segment] = (
    (JsPath \ "start").read[Float] and
      (JsPath \ "duration").read[Float] and
      (JsPath \ "confidence").read[Float] and
      (JsPath \ "loudness_start").read[Float] and
      (JsPath \ "loudness_max_time").read[Float] and
      (JsPath \ "loudness_max").read[Float] and
      (JsPath \ "loudness_end").readNullable[Float] and
      (JsPath \ "pitches").read[Seq[Float]] and
      (JsPath \ "timbre").read[Seq[Float]]
    ) (Segment.apply _)
}
// ===================================================================================================================

case class TrackAnalysis(num_samples: Option[Long], duration: Float, sample_md5: String, offset_seconds: Int,
                         window_seconds: Int, analysis_sample_rate: Long, analysis_channels: Int,
                         end_of_fade_in: Float, start_of_fade_out: Float, loudness: Float, tempo: Float,
                         tempo_confidence: Float, time_signature: Int, time_signature_confidence: Float,
                         key: Int, key_confidence: Float, mode: Int, mode_confidence: Float)

object TrackAnalysis {
  implicit val trackAnalysisReads: Reads[TrackAnalysis] = (
    (JsPath \ "num_samples").readNullable[Long] and
      (JsPath \ "duration").read[Float] and
      (JsPath \ "sample_md5").read[String] and
      (JsPath \ "offset_seconds").read[Int] and
      (JsPath \ "window_seconds").read[Int] and
      (JsPath \ "analysis_sample_rate").read[Long] and
      (JsPath \ "analysis_channels").read[Int] and
      (JsPath \ "end_of_fade_in").read[Float] and
      (JsPath \ "start_of_fade_out").read[Float] and
      (JsPath \ "loudness").read[Float] and
      (JsPath \ "tempo").read[Float] and
      (JsPath \ "tempo_confidence").read[Float] and
      (JsPath \ "time_signature").read[Int] and
      (JsPath \ "time_signature_confidence").read[Float] and
      (JsPath \ "key").read[Int] and
      (JsPath \ "key_confidence").read[Float] and
      (JsPath \ "mode").read[Int] and
      (JsPath \ "mode_confidence").read[Float]
    ) (TrackAnalysis.apply _)
}
// =====================================================================================================================

case class MetaAnalysis(analyzer_version: String, platform: Option[String], detailed_status: String,
                        bitrate: Option[Long], sample_rate: Option[Long], seconds: Option[Long], status_code: Int,
                        timestamp: Long, analysis_time: Float, input_process: Option[String])

object MetaAnalysis {
  implicit val metaAnalysisReads: Reads[MetaAnalysis] = (
    (JsPath \ "analyzer_version").read[String] and
      (JsPath \ "platform").readNullable[String] and
      (JsPath \ "detailed_status").read[String] and
      (JsPath \ "bitrate").readNullable[Long] and
      (JsPath \ "sample_rate").readNullable[Long] and
      (JsPath \ "seconds").readNullable[Long] and
      (JsPath \ "status_code").read[Int] and
      (JsPath \ "timestamp").read[Long] and
      (JsPath \ "analysis_time").read[Float] and
      (JsPath \ "input_process").readNullable[String]
    ) (MetaAnalysis.apply _)
}
// =====================================================================================================================
