package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class Page[T]
(
href: String,
limit: Int,
next: Option[String],
offset: Int,
previous: Option[String],
total: Int,
items: List[T]
)

object Page {

  private val jsPage = {
    (JsPath \ "href").read[String] and
      (JsPath \ "limit").read[Int] and
      (JsPath \ "next").readNullable[String] and
      (JsPath \ "offset").read[Int] and
      (JsPath \ "previous").readNullable[String] and
      (JsPath \ "total").read[Int]
  }

  implicit val featuredPlaylistsReads: Reads[Page[SimplePlaylist]] = (
    jsPage and (JsPath \ "items").read[List[SimplePlaylist]]
    ) (Page.apply[SimplePlaylist] _)

  implicit val simpleTracksReads: Reads[Page[SimpleTrack]] = (
    jsPage and (JsPath \ "items").read[List[SimpleTrack]]
    ) (Page.apply[SimpleTrack] _)

  implicit val albumReads: Reads[Page[SimpleAlbum]] = (
    jsPage and (JsPath \ "items").read[List[SimpleAlbum]]
    ) (Page.apply[SimpleAlbum] _)

  implicit val trackReads: Reads[Page[Track]] = (
    jsPage and (JsPath \ "items").read[List[Track]]
    ) (Page.apply[Track] _)

  implicit val playlistTrackReads: Reads[Page[PlaylistTrack]] = (
    jsPage and (JsPath \ "items").read[List[PlaylistTrack]]
    ) (Page.apply[PlaylistTrack] _)

  /*
  implicit def pageReads[T](implicit fmt: Reads[T]): Reads[Page[T]] = new Reads[Page[T]] {
    override def reads(json: JsValue): JsResult[Page[T]] = JsSuccess(new Page[T](
      (json \ "href").as[String],
      (json \ "limit").as[Int],
      (json \ "next").asOpt[String],
      (json \ "offset").as[Int],
      (json \ "previous").asOpt[String],
      (json \ "total").as[Int],
      json \ "items" match {
        case JsDefined(JsArray(ts)) => ts.map(t => json.as[T](fmt)).toList
        case _ => throw new RuntimeException("Page items object must be a List")
      }
    ))
  }
  */

}

