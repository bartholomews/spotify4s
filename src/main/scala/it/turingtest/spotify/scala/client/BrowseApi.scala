package it.turingtest.spotify.scala.client

import java.util.Locale
import javax.inject.Inject

import com.vitorsvieira.iso.ISOCountry.ISOCountry
import it.turingtest.spotify.scala.client.entities._
import it.turingtest.spotify.scala.client.logging.AccessLogging
import it.turingtest.spotify.scala.client.utils.ConversionUtils
import org.joda.time.LocalDateTime

import scala.concurrent.Future

/**
  * @see https://developer.spotify.com/web-api/browse-endpoints/
  */
class BrowseApi @Inject()(api: BaseApi) extends AccessLogging {

  private val BROWSE = s"${api.BASE_URL}/browse"

  // =====================================================================================================================
  /**
    * @see https://developer.spotify.com/web-api/get-list-featured-playlists/
    */
  private final val FEATURED_PLAYLISTS = s"$BROWSE/featured-playlists"

  def featuredPlaylists: Future[FeaturedPlaylists] = api.get[FeaturedPlaylists](FEATURED_PLAYLISTS)

  def featuredPlaylists(locale: Option[String] = None, country: Option[ISOCountry] = None,
                        timestamp: Option[LocalDateTime] = None,
                        limit: Int = 20, offset: Int = 0): Future[FeaturedPlaylists] = {

    val query = ConversionUtils.seq(
      ("locale", locale), ("country", country), ("timestamp", timestamp)
    ) ++ Seq(("limit", limit.toString), ("offset", offset.toString))

    api.get[FeaturedPlaylists](FEATURED_PLAYLISTS, query.toList: _*)
  }

  // ===================================================================================================================
  /**
    * @see https://developer.spotify.com/web-api/get-list-new-releases/
    */
  private final val NEW_RELEASES = s"$BROWSE/new-releases"

  def newReleases: Future[NewReleases] = api.get[NewReleases](NEW_RELEASES)

  def newReleases(country: Option[ISOCountry] = None, limit: Int = 20, offset: Int = 0): Future[NewReleases] = {
    val query: Seq[(String, String)] = ConversionUtils.seq(
      ("country", country)) ++ Seq(Some("limit", limit.toString), Some("offset", offset.toString)).flatten

    api.get[NewReleases](NEW_RELEASES, query.toList: _*)
  }

  // ===================================================================================================================

  private final val CATEGORIES = s"$BROWSE/categories"

  /**
    * @see https://developer.spotify.com/web-api/get-list-categories/
    * TODO could poll and keep a list of CATEGORY_ID objects instead of free strings
    * to be used in categoryPlaylist(category_id)
    */
  def categories(country: Option[ISOCountry] = None, locale: Option[Locale] = None,
                 limit: Int = 20, offset: Int = 0): Future[Page[Category]] = {

    val query: Seq[(String, String)] = ConversionUtils.seq(
      ("country", country), ("locale", locale)) ++ Seq(Some("limit", limit.toString), Some("offset", offset.toString)).flatten

    api.get[Page[Category]]("categories", CATEGORIES, query.toList: _*)
  }

  /**
    * @see https://developer.spotify.com/web-api/get-category/
    */
  def category(id: String, country: Option[ISOCountry] = None, locale: Option[Locale] = None): Future[Category] = {
    val query = ConversionUtils.seq(("country", country), ("locale", locale))
    api.get[Category](s"$CATEGORIES/$id", query.toList: _*)
  }

  /**
    * @see https://developer.spotify.com/web-api/get-categorys-playlists/
    */
  def categoryPlaylists(category_id: String, country: Option[ISOCountry] = None,
                        limit: Int = 20, offset: Int = 0): Future[Page[SimplePlaylist]] = {

    val query = ConversionUtils.seq(("country", country)) ++ Seq(Some("limit", limit.toString), Some("offset", offset.toString)).flatten
    api.get[Page[SimplePlaylist]]("playlists", s"$CATEGORIES/$category_id/playlists", query.toList: _*)
  }

  // ===================================================================================================================

  /**
    * @see https://developer.spotify.com/web-api/get-recommendations/
    */
  private final val RECOMMENDATIONS = s"$BROWSE/recommendations"

  val NONE_3 = (None, None, None)

  def getRecommendation(limit: Int = 20,
                        market: Option[ISOCountry] = None,
                        acousticness_range: (Option[Float], Option[Float], Option[Float]) = NONE_3,
                        danceability_range: (Option[Float], Option[Float], Option[Float]) = NONE_3,
                        duration_ms_range: (Option[Int], Option[Int], Option[Int]) = NONE_3,
                        energy_range: (Option[Float], Option[Float], Option[Float]) = NONE_3,
                        instrumentalness_range: (Option[Float], Option[Float], Option[Float]) = NONE_3,
                        key_range: (Option[Int], Option[Int], Option[Int]) = NONE_3,
                        liveness_range: (Option[Float], Option[Float], Option[Float]) = NONE_3,
                        loudness_range: (Option[Float], Option[Float], Option[Float]) = NONE_3,
                        mode_range: (Option[Int], Option[Int], Option[Int]) = NONE_3,
                        popularity_range: (Option[Int], Option[Int], Option[Int]) = NONE_3,
                        speechiness_range: (Option[Float], Option[Float], Option[Float]) = NONE_3,
                        tempo_range: (Option[Float], Option[Float], Option[Float]) = NONE_3,
                        time_signature_range: (Option[Int], Option[Int], Option[Int]) = NONE_3,
                        valence_range: (Option[Float], Option[Float], Option[Float]) = NONE_3,
                        seed_artists: Seq[String] = Nil, seed_genres: Seq[String] = Nil,
                        seed_tracks: Seq[String] = Nil): Future[Recommendation] = {

    def seedsQuery(seed: Seq[String]): Option[String] = {
      Option(seed.mkString(",")).filter(_.nonEmpty)
    }

    def attrs[T](key: String, value: (Option[T], Option[T], Option[T]))
                (implicit n: Numeric[T]): Seq[(String, String)] = {

      val min = value._1.map(v => (s"min_$key", v.toString))
      val target = value._2.map(v => (s"target_$key", v.toString))
      val max = value._3.map(v => (s"max_$key", v.toString))

      Seq(min, target, max).flatten
    }

    val query = ConversionUtils.seq(
      ("limit", Option(limit)),
      ("market", market),
      ("seed_artists", seedsQuery(seed_artists)),
      ("seed_genres", seedsQuery(seed_genres)),
      ("seed_tracks", seedsQuery(seed_tracks))
    ) ++ attrs("acousticnesss", acousticness_range) ++
      attrs("danceability", danceability_range) ++
      attrs("duration_ms", duration_ms_range) ++
      attrs("energy", energy_range) ++
      attrs("instrumentalness", instrumentalness_range) ++
      attrs("key", key_range) ++
      attrs("liveness", liveness_range) ++
      attrs("loudness", loudness_range) ++
      attrs("mode", mode_range) ++
      attrs("popularity", popularity_range)

    api.get[Recommendation](s"$RECOMMENDATIONS", query.toList: _*)
  }
}
