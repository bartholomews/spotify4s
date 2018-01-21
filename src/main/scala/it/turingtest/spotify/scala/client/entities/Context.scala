package it.turingtest.spotify.scala.client.entities

import scala.util.Try

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsonValidationError, Reads}

/**
  * @see https://developer.spotify.com/web-api/get-information-about-the-users-current-playback/
  *
  * @param uri The uri of the context.
  * @param href The href of the context, if available.
  * @param external_urls The external_urls of the context, if available.
  * @param contextType 	The object type of the item's context.
  */
case class Context(uri: String,
                   href: Option[String],
                   external_urls: Option[ExternalURL],
                   contextType: ContextType)

object Context {
  implicit val contextReads: Reads[Context] = (
    (JsPath \ "uri").read[String] and
      (JsPath \ "href").readNullable[String] and
      (JsPath \ "external_url").readNullable[ExternalURL] and
      // @see https://stackoverflow.com/a/29948007
      (JsPath \ "type").read[String].collect(JsonValidationError(""))(
        Function.unlift(t => Try(ContextType.valueOf(t)).toOption))
    )(Context.apply _)
}

sealed trait ContextType

object ContextType {
  def valueOf(value: String): ContextType = value.toLowerCase match {
    case "album" => AlbumContext
    case "artist" => ArtistContext
    case "playlist" => PlaylistContext
    case other => throw new IllegalArgumentException(s"$other: invalid context type.")
  }
}

case object AlbumContext extends ContextType
case object ArtistContext extends ContextType
case object PlaylistContext extends ContextType
