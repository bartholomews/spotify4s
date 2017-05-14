import javax.inject.Inject

import it.turingtest.spotify.scala.client.entities.FeaturedPlaylists
import it.turingtest.spotify.scala.client.logging.AccessLogging
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  *
  */
class BrowseApi @Inject()(ws: WSClient, api: BaseApi) extends AccessLogging {

  private val BROWSE = s"${api.BASE_URL}/browse"

  // =====================================================================================================================
  /**
    * https://developer.spotify.com/web-api/get-list-featured-playlists/
    */
  private final val FEATURED_PLAYLISTS = s"$BROWSE/featured-playlists"

  def featuredPlaylists(action: FeaturedPlaylists => Result): Action[AnyContent] = Action.async {
    featuredPlaylists map { p: FeaturedPlaylists => action(p) }
  }

  def featuredPlaylists: Future[FeaturedPlaylists] = api.get[FeaturedPlaylists](FEATURED_PLAYLISTS)

  // ===================================================================================================================
  /**
    * https://developer.spotify.com/web-api/get-list-new-releases/
    */
  private final val NEW_RELEASES = s"$BROWSE/new-releases"

  /*
  def newReleases: Future[List[SimpleAlbum]] = {
    def loop(call: String, acc: List[SimpleAlbum]): Future[List[SimpleAlbum]] = {
      api.get[NewReleases](call) flatMap {
        p: NewReleases =>
          p.albums.next match {
          case None => Future(p.albums.items ::: acc)
          case Some(href) => loop(href, p.albums.items ::: acc)
        }
      }
    }
    loop(NEW_RELEASES, List())
  }
  */

  /*
  private def getNewReleasesList(token: String, query: Option[String] = None): Future[WSResponse] = {
    ws.url(query.getOrElse(NEW_RELEASES))
      .withHeaders(auth_bearer(token))
      .withQueryString(
        "" -> "" // TODO
      )
      .get()
  }
  */

  // ===================================================================================================================

}
