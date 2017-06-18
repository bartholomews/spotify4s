package it.turingtest.spotify.scala.client

import javax.inject.Inject

import it.turingtest.spotify.scala.client.entities.{FeaturedPlaylists, NewReleases}
import it.turingtest.spotify.scala.client.logging.AccessLogging
import it.turingtest.spotify.scala.client.utils.ConversionUtils
import org.joda.time.LocalDateTime
import play.api.mvc.{Action, AnyContent, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @see https://developer.spotify.com/web-api/browse-endpoints/
  */
class BrowseApi @Inject()(api: BaseApi) extends AccessLogging {

  private val BROWSE = s"${api.BASE_URL}/browse"

  // =====================================================================================================================
  /**
    * https://developer.spotify.com/web-api/get-list-featured-playlists/
    */
  private final val FEATURED_PLAYLISTS = s"$BROWSE/featured-playlists"

  def featuredPlaylists: Future[FeaturedPlaylists] = api.get[FeaturedPlaylists](FEATURED_PLAYLISTS)

  def featuredPlaylists(locale: Option[String] = None, country: Option[String] = None,
                        timestamp: Option[LocalDateTime] = None,
                        limit: Int = 20, offset: Int = 0): Future[FeaturedPlaylists] = {

    val query = ConversionUtils.seq(
      ("locale", locale), ("country", country), ("timestamp", timestamp)
    ) ++ Seq(("limit", limit.toString), ("offset", offset.toString))

    api.get[FeaturedPlaylists](FEATURED_PLAYLISTS, query.toList: _*)
  }

  // ===================================================================================================================
  /**
    * https://developer.spotify.com/web-api/get-list-new-releases/
    */
  private final val NEW_RELEASES = s"$BROWSE/new-releases"

  def newReleases: Future[NewReleases] = api.get[NewReleases](NEW_RELEASES)

  def newReleases(country: Option[String] = None, limit: Int = 20, offset: Int = 0): Future[NewReleases] = {
    val query: Seq[(String, String)] = Seq(
      country.map(c => ("country", c)),
      Some("limit", limit.toString),
      Some("offset", offset.toString)).flatten

    api.get[NewReleases](NEW_RELEASES, query.toList: _*)
  }

  // ===================================================================================================================

}
