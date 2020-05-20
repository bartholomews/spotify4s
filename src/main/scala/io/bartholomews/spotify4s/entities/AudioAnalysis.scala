package io.bartholomews.spotify4s.entities

import io.bartholomews.fsclient.codecs.FsJsonResponsePipe
import io.circe.generic.extras.ConfiguredJsonCodec

// https://developer.spotify.com/console/get-audio-analysis-track/?id=06AKEBrKUckW0KREUWRnvT
@ConfiguredJsonCodec
case class AudioAnalysis(
  bars: List[Bar],
  beats: List[Beat],
  sections: List[AudioSection],
  segments: List[AudioSegment],
  tatums: List[Tatum]
)

object AudioAnalysis extends FsJsonResponsePipe[AudioAnalysis]
