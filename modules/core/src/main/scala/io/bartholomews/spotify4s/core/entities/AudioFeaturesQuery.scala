package io.bartholomews.spotify4s.core.entities

import scala.concurrent.duration.FiniteDuration

final case class AudioFeaturesQuery(
  acousticness: AudioFeatureParams[Double] = AudioFeatureParams(),
  danceability: AudioFeatureParams[Confidence] = AudioFeatureParams(),
  duration: AudioFeatureParams[FiniteDuration] = AudioFeatureParams(),
  energy: AudioFeatureParams[Confidence] = AudioFeatureParams(),
  instrumentalness: AudioFeatureParams[Confidence] = AudioFeatureParams(),
  key: AudioFeatureParams[Int] = AudioFeatureParams(),
  liveness: AudioFeatureParams[Confidence] = AudioFeatureParams(),
  loudness: AudioFeatureParams[Double] = AudioFeatureParams(),
  mode: AudioFeatureParams[Int] = AudioFeatureParams(),
  popularity: AudioFeatureParams[Confidence] = AudioFeatureParams(),
  speechiness: AudioFeatureParams[Confidence] = AudioFeatureParams(),
  tempo: AudioFeatureParams[Double] = AudioFeatureParams(), // TODO bpm type
  timeSignature: AudioFeatureParams[Int] = AudioFeatureParams(),
  valence: AudioFeatureParams[Confidence] = AudioFeatureParams()
)

object AudioFeaturesQuery {
  def empty: AudioFeaturesQuery = new AudioFeaturesQuery()

  private implicit val encodeParamInt: EncodeParam[Int] = EncodeParam.fromToString
  private implicit val encodeParamDouble: EncodeParam[Double] = EncodeParam.fromToString
  private implicit val encodeParamConfidence: EncodeParam[Confidence] = encodeParamDouble.contramap(_.value)
  private implicit val encodeParamDurationMs: EncodeParam[FiniteDuration] = (value: FiniteDuration) =>
    value.toMillis.toString

  private[spotify4s] final def toParams(q: AudioFeaturesQuery): List[(String, String)] =
    q.acousticness.toList(key = "acousticness") ++
      q.danceability.toList(key = "danceability") ++
      q.duration.toList(key = "duration_ms") ++
      q.energy.toList(key = "energy") ++
      q.instrumentalness.toList(key = "instrumentalness") ++
      q.key.toList(key = "key") ++
      q.liveness.toList(key = "liveness") ++
      q.loudness.toList(key = "loudness") ++
      q.mode.toList(key = "mode") ++
      q.popularity.toList(key = "popularity") ++
      q.speechiness.toList(key = "speechiness") ++
      q.tempo.toList(key = "tempo") ++
      q.timeSignature.toList(key = "time_signature") ++
      q.valence.toList(key = "valence")
}

private[spotify4s] object EncodeParam {
  def fromToString[A]: EncodeParam[A] = _.toString
}

final case class AudioFeatureParams[A](
  min: Option[A] = None,
  max: Option[A] = None,
  target: Option[A] = None
) {
  private[spotify4s] def toList(key: String)(implicit encodeParam: EncodeParam[A]): List[(String, String)] =
    min.map(value => (s"min_$key", encodeParam.encode(value))).toList ++
      max.map(value => (s"max_$key", encodeParam.encode(value))).toList ++
      target.map(value => (s"target_$key", encodeParam.encode(value))).toList
}

private[spotify4s] trait EncodeParam[A] {
  def encode(value: A): String
  final def contramap[B](f: B => A): EncodeParam[B] = (value: B) => encode(f(value))
}
