package io.bartholomews.spotify4s.core.entities

import scala.concurrent.duration.FiniteDuration

sealed trait AudioFeatureQuery[A] {
  def min: A
  def max: A
  def target: A
}

final case class AcousticnessQuery(min: Double, max: Double, target: Double) extends AudioFeatureQuery[Double]

final case class DanceabilityQuery(min: Confidence, max: Confidence, target: Confidence)
    extends AudioFeatureQuery[Confidence]

final case class DurationQuery(min: FiniteDuration, max: FiniteDuration, target: FiniteDuration)
    extends AudioFeatureQuery[FiniteDuration]

final case class EnergyQuery(min: Confidence, max: Confidence, target: Confidence) extends AudioFeatureQuery[Confidence]

final case class InstrumentalnessQuery(min: Confidence, max: Confidence, target: Confidence)
    extends AudioFeatureQuery[Confidence]

final case class KeyQuery(min: Int, max: Int, target: Int) extends AudioFeatureQuery[Int]

final case class LivenessQuery(min: Confidence, max: Confidence, target: Confidence)
    extends AudioFeatureQuery[Confidence]

final case class LoudnessQuery(min: Double, max: Double, target: Double) extends AudioFeatureQuery[Double]

final case class ModeQuery(min: Int, max: Int, target: Int) extends AudioFeatureQuery[Int]

final case class PopularityQuery(min: Confidence, max: Confidence, target: Confidence)
    extends AudioFeatureQuery[Confidence]

final case class SpeechinessQuery(min: Confidence, max: Confidence, target: Confidence)
    extends AudioFeatureQuery[Confidence]

final case class TempoQuery(min: Double, max: Double, target: Double) extends AudioFeatureQuery[Double]

final case class TimeSignatureQuery(min: Int, max: Int, target: Int) extends AudioFeatureQuery[Int]

final case class ValenceQuery(min: Confidence, max: Confidence, target: Confidence)
    extends AudioFeatureQuery[Confidence]
