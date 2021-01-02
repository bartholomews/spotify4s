package io.bartholomews.spotify4s.core.entities

import io.bartholomews.spotify4s.core.entities.TimeInterval.{Bar, Beat, Tatum}

// https://developer.spotify.com/console/get-audio-analysis-track/?id=06AKEBrKUckW0KREUWRnvT
case class AudioAnalysis(
  bars: List[Bar],
  beats: List[Beat],
  sections: List[AudioSection],
  segments: List[AudioSegment],
  tatums: List[Tatum]
)
