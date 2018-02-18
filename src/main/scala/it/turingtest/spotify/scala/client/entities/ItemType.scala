package it.turingtest.spotify.scala.client.entities

import play.api.libs.json._

sealed trait ItemType {
  def value: String
}

object ItemType {
  case object Album extends ItemType { def value: String = "album" }
  case object Artist extends ItemType { def value: String = "artist" }
  case object Playlist extends ItemType { def value: String = "playlist" }
  case object Track extends ItemType { def value: String = "track" }

  implicit val reader: Reads[ItemType] = new Reads[ItemType] {
    override def reads(json: JsValue): JsResult[ItemType] = json match {
      case JsString("album") => JsSuccess(ItemType.Album)
      case JsString("artist") => JsSuccess(ItemType.Artist)
      case JsString("playlist") => JsSuccess(ItemType.Playlist)
      case JsString("track") => JsSuccess(ItemType.Track)
      case other => JsError(s"Cannot parse ItemType from json '$other'")
    }
  }
}
