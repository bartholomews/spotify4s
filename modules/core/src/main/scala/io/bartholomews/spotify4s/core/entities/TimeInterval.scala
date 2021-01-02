package io.bartholomews.spotify4s.core.entities

// https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-analysis/#time-interval-object
sealed trait TimeInterval {
  def start: Double
  def duration: Double
  def confidence: Confidence
}

object TimeInterval {
  case class Bar(start: Double, duration: Double, confidence: Confidence) extends TimeInterval
  case class Beat(start: Double, duration: Double, confidence: Confidence) extends TimeInterval
  case class Tatum(start: Double, duration: Double, confidence: Confidence) extends TimeInterval
}
