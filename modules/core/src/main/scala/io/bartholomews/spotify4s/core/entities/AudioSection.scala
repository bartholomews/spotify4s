package io.bartholomews.spotify4s.core.entities

// https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-analysis/#section-object
case class AudioSection(
  start: Double,
  duration: Double,
  confidence: Confidence,
  loudness: Double,
  tempo: Tempo,
  key: AudioKey,
  mode: AudioMode,
  timeSignature: TimeSignature
)

case class Tempo(value: Double, confidence: Confidence)
case class AudioKey(value: Option[PitchClass], confidence: Confidence)
case class AudioMode(value: Modality, confidence: Confidence)
case class TimeSignature(value: Double, confidence: Confidence)
