package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.spotify4s.core.api.TracksApiSpec
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.playJson.{PlayEntityCodecs, PlayServerBehaviours}
import play.api.libs.json.{JsError, JsValue, Reads, Writes}

class TracksApiPlaySpec
    extends TracksApiSpec[Writes, Reads, JsError, JsValue]
    with PlayServerBehaviours
    with PlayEntityCodecs {
  override implicit def audioAnalysisDecoder: Reads[AudioAnalysis] = audioAnalysisCodec
  override implicit def audioFeaturesDecoder: Reads[AudioFeatures] = audioFeaturesCodec
  override implicit def audioFeaturesResponseDecoder: Reads[AudioFeaturesResponse] = audioFeaturesResponseCodec
  override implicit def fullTrackDecoder: Reads[FullTrack] = fullTrackCodec
  override implicit def fullTracksResponseDecoder: Reads[FullTracksResponse] = fullTracksResponseCodec
}
