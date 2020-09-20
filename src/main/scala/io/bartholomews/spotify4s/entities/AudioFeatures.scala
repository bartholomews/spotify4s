package io.bartholomews.spotify4s.entities

import io.bartholomews.fsclient.codecs.FsJsonResponsePipe
import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder
import org.http4s.Uri

case class AudioFeatures(
  durationMs: Int,
  key: PitchClass,
  mode: Modality,
  timeSignature: Int,
  acousticness: Confidence,
  danceability: Confidence,
  energy: Confidence,
  instrumentalness: Confidence,
  liveness: Confidence,
  loudness: Double, // Values typical range between -60 and 0 db.
  speechiness: Confidence,
  valence: Confidence,
  tempo: Double,
  id: SpotifyId,
  uri: SpotifyUri,
  trackHref: Uri,
  analysisUrl: Uri
)

object AudioFeatures extends FsJsonResponsePipe[AudioFeatures] {
  implicit val decoder: Decoder[AudioFeatures] = deriveConfiguredDecoder
}
