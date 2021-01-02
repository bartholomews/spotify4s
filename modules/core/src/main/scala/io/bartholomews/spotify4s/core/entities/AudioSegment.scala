package io.bartholomews.spotify4s.core.entities

case class AudioSegment(
  start: Double,
  duration: Double,
  confidence: Confidence,
  loudness: Loudness,
  pitches: List[Double], // Todo 0 to 12 Float?
  timbre: List[Double]
)

case class Loudness(
  start: Double,
  max: Double,
  maxTime: Double,
  end: Option[Double]
)
