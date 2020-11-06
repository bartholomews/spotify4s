package io.bartholomews.spotify4s.entities

import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder

// https://developer.spotify.com/console/get-audio-analysis-track/?id=06AKEBrKUckW0KREUWRnvT
case class AudioAnalysis(
  bars: List[Bar],
  beats: List[Beat],
  sections: List[AudioSection],
  segments: List[AudioSegment],
  tatums: List[Tatum]
)

object AudioAnalysis {
  implicit val decoder: Decoder[AudioAnalysis] = deriveConfiguredDecoder
}
