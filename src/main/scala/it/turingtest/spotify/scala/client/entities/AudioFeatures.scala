package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

/**
  * @see https://developer.spotify.com/web-api/object-model/#audio-features-object
  *
  * @param objectType Spotify type ("audio_features")
  *
  * @param acousticness A confidence measure from 0.0 to 1.0 of whether the track is acoustic.
  *                     1.0 represents high confidence the track is acoustic.
  *
  * @param analysis_url An HTTP URL to access the full audio analysis of this track.
  *                     An access token is required to access this data.
  *
  * @param danceability Danceability describes how suitable a track is for dancing
  *                     based on a combination of musical elements including tempo,
  *                     rhythm stability, beat strength, and overall regularity.
  *                     A value of 0.0 is least danceable and 1.0 is most danceable.
  *
  * @param duration_ms The duration of the track in milliseconds.
  *
  * @param energy Energy is a measure from 0.0 to 1.0 and represents a perceptual measure of intensity and activity.
  *               Typically, energetic tracks feel fast, loud, and noisy.
  *               For example, death metal has high energy, while a Bach prelude scores low on the scale.
  *               Perceptual features contributing to this attribute include dynamic range,
  *               perceived loudness, timbre, onset rate, and general entropy.
  *
  * @param id The Spotify ID for the track.
  *
  * @param instrumentalness Predicts whether a track contains no vocals.
  *                         "Ooh" and "aah" sounds are treated as instrumental in this context.
  *                         Rap or spoken word tracks are clearly "vocal".
  *                         The closer the instrumentalness value is to 1.0,
  *                         the greater likelihood the track contains no vocal content.
  *                         Values above 0.5 are intended to represent instrumental tracks,
  *                         but confidence is higher as the value approaches 1.0.
  *
  * @param key The key the track is in. Integers map to pitches using standard Pitch Class notation. (see https://en.wikipedia.org/wiki/Pitch_class)
  *            E.g. 0 = C, 1 = C♯/D♭, 2 = D, and so on.
  *
  * @param liveness Detects the presence of an audience in the recording.
  *                 Higher liveness values represent an increased probability that the track was performed live.
  *                 A value above 0.8 provides strong likelihood that the track is live.
  *
  * @param loudness The overall loudness of a track in decibels (dB).
  *                 Loudness values are averaged across the entire track
  *                 and are useful for comparing relative loudness of tracks.
  *                 Loudness is the quality of a sound that is the primary psychological
  *                 correlate of physical strength (amplitude). Values typical range between -60 and 0 db.
  *
  * @param mode Mode indicates the modality (major or minor) of a track,
  *             the type of scale from which its melodic content is derived.
  *             Major is represented by 1 and minor is 0.
  *
  * @param speechiness Speechiness detects the presence of spoken words in a track.
  *                    The more exclusively speech-like the recording (e.g. talk show, audio book, poetry),
  *                    the closer to 1.0 the attribute value.
  *                    Values above 0.66 describe tracks that are probably made entirely of spoken words.
  *                    Values between 0.33 and 0.66 describe tracks that may contain both music and speech,
  *                    either in sections or layered, including such cases as rap music.
  *                    Values below 0.33 most likely represent music and other non-speech-like tracks.
  *
  * @param tempo The overall estimated tempo of a track in beats per minute (BPM).
  *              In musical terminology, tempo is the speed or pace of a given piece
  *              and derives directly from the average beat duration.
  *
  * @param time_signature An estimated overall time signature of a track.
  *                       The time signature (meter) is a notational convention
  *                       to specify how many beats are in each bar (or measure).
  *
  * @param track_href A link to the Web API endpoint providing full details of the track.
  *
 * @param uri The Spotify URI for the track.
  *
 * @param valence A measure from 0.0 to 1.0 describing the musical positiveness conveyed by a track. Tracks with high valence sound more positive (e.g. happy, cheerful, euphoric), while tracks with low valence sound more negative (e.g. sad, depressed, angry).
  *
  */
case class AudioFeatures
( override val objectType: String,
  acousticness: Float,
  analysis_url: String,
  danceability: Float,
  duration_ms: Float,
  energy: Float,
  id: String,
  instrumentalness: Float,
  key: Int,
  liveness: Float,
  loudness: Float,
  mode: Int,
  speechiness: Float,
  tempo: Float,
  time_signature: Int,
  track_href: String,
  uri: String,
  valence: Float) extends SpotifyObject

object AudioFeatures {
  implicit val audioFeaturesReads: Reads[AudioFeatures] = (
    (JsPath \ "type").read[String] and
      (JsPath \ "acousticness").read[Float] and
      (JsPath \ "analysis_url").read[String] and
      (JsPath \ "danceability").read[Float] and
      (JsPath \ "duration_ms").read[Float] and
      (JsPath \ "energy").read[Float] and
      (JsPath \ "id").read[String] and
      (JsPath \ "instrumentalness").read[Float] and
      (JsPath \ "key").read[Int] and
      (JsPath \ "liveness").read[Float] and
      (JsPath \ "loudness").read[Float] and
      (JsPath \ "mode").read[Int] and
      (JsPath \ "speechiness").read[Float] and
      (JsPath \ "tempo").read[Float] and
      (JsPath \ "time_signature").read[Int] and
      (JsPath \ "track_href").read[String] and
      (JsPath \ "uri").read[String] and
      (JsPath \ "valence").read[Float]
    )(AudioFeatures.apply _)

}
