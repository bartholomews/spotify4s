package it.turingtest.spotify.scala.client

import javax.inject.Inject

import com.vitorsvieira.iso.ISOCountry.ISOCountry
import it.turingtest.spotify.scala.client.entities._
import it.turingtest.spotify.scala.client.logging.AccessLogging

import scala.concurrent.Future

/**
  * @see https://developer.spotify.com/web-api/track-endpoints/
  */
class TracksApi @Inject()(api: BaseApi) extends AccessLogging {

  private final val TRACKS = s"${api.BASE_URL}/tracks"
  private final val AUDIO_FEATURES = s"${api.BASE_URL}/audio-features"
  private final val AUDIO_ANALYSIS = s"${api.BASE_URL}/audio-analysis"

  // =====================================================================================================================

  /**
    * @see https://developer.spotify.com/web-api/get-audio-analysis/
    * @param id The Spotify ID for the track.
    * @return an `AudioAnalysis` object
    */
  def getAudioAnalysis(id: String): Future[AudioAnalysis] = api.getWithToken[AudioAnalysis](s"$AUDIO_ANALYSIS/$id")

  /**
    * @see https://developer.spotify.com/web-api/get-audio-features/
    * @param id The Spotify ID for the track.
    * @return an `AudioFeatures` object
    */
  def getAudioFeatures(id: String): Future[AudioFeatures] = api.getWithToken[AudioFeatures](s"$AUDIO_FEATURES/$id")

  /**
    * @see https://developer.spotify.com/web-api/get-several-audio-features/
    * @param ids The Spotify IDs for the tracks. Maximum: 50 IDs.
    * @return a sequence of `AudioFeatures` objects
    */
  def getAudioFeatures(ids: Seq[String]): Future[Seq[AudioFeatures]] = {
    println(ids.mkString(","))
    api.getWithToken[Seq[AudioFeatures]]("audio_features", s"$AUDIO_FEATURES/", ("ids", ids.mkString(",")))
  }

  /**
    * @see https://developer.spotify.com/web-api/get-track/
    * @param id The Spotify ID for the track.
    * @param market Optional. An ISO 3166-1 alpha-2 country code.
    *               Provide this parameter if you want to apply Track Relinking.
    * @return a `Track` object
    */
  def getTrack(id: String, market: Option[ISOCountry] = None): Future[Track] = market match {
    case Some(country) => api.getWithToken[Track](s"$TRACKS/$id", ("market", country.value))
    case None => api.getWithToken[Track](s"$TRACKS/$id")
  }

  /**
    * @see https://developer.spotify.com/web-api/get-several-tracks/
    * @param ids The Spotify IDs for the tracks. Maximum: 50 IDs.
    * @param market Optional. An ISO 3166-1 alpha-2 country code.
    *               Provide this parameter if you want to apply Track Relinking.
    * @return a sequence of `Track` objects
    */
  def getTracks(ids: Seq[String], market: Option[ISOCountry] = None): Future[Seq[Track]] = {
    val query = ("ids", ids.mkString(","))
    market match {
      case Some(country) => api.getWithToken[Seq[Track]]("tracks", s"$TRACKS/", query, ("market", country.value))
      case None => api.getWithToken[Seq[Track]]("tracks", s"$TRACKS/", query)
    }
  }

}
