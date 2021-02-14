package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.spotify4s.core.api.TracksApiSpec
import io.bartholomews.spotify4s.core.entities.{
  AudioAnalysis,
  AudioFeatures,
  AudioFeaturesResponse,
  FullTrack,
  FullTracksResponse
}
import io.bartholomews.spotify4s.playJson.PlayServerBehaviours
import play.api.libs.json.{JsError, Reads, Writes}

class TracksApiPlaySpec extends TracksApiSpec[Writes, Reads, JsError] with PlayServerBehaviours {
  override implicit def audioAnalysisDecoder: Reads[AudioAnalysis] = audioAnalysisCodec
  override implicit def audioFeaturesDecoder: Reads[AudioFeatures] = audioFeaturesCodec
  override implicit def audioFeaturesResponseDecoder: Reads[AudioFeaturesResponse] = audioFeaturesResponseCodec
  override implicit def fullTrackDecoder: Reads[FullTrack] = fullTrackCodec
  override implicit def fullTracksResponseDecoder: Reads[FullTracksResponse] = fullTracksResponseCodec
}
