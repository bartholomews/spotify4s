package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class User
(
  display_name: Option[String],
  external_urls: ExternalURL,
  followers: Option[Followers],
  href: String,
  id: String,
  images: Option[List[Image]],
  objectType: String,
  uri: String
)

object User {
  implicit val userReads: Reads[User] = (
    (JsPath \ "display_name").readNullable[String] and
      (JsPath \ "external_urls").read[ExternalURL] and
      (JsPath \ "followers").readNullable[Followers] and
      (JsPath \ "href").read[String] and
      (JsPath \ "id").read[String] and
      (JsPath \ "images").readNullable[List[Image]] and
      (JsPath \ "type").read[String] and
      (JsPath \ "uri").read[String]
    )(User.apply _)
}
