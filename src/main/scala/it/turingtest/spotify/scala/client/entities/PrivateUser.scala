package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class PrivateUser
(
  birthdate: String,
  country: String,
  display_name: String,
  email: String,
  external_urls: ExternalURL,
  followers: Followers,
  href: String,
  id: String,
  images: List[Image],
  product: String,
  objectType: String,
  uri: String
)

object PrivateUser {
  implicit val privateUserReads: Reads[PrivateUser] = (
    (JsPath \ "birthdate").read[String] and
      (JsPath \ "country").read[String] and
      (JsPath \ "display_name").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "external_urls").read[ExternalURL] and
      (JsPath \ "followers").read[Followers] and
      (JsPath \ "href").read[String] and
      (JsPath \ "id").read[String] and
      (JsPath \ "images").read[List[Image]] and
      (JsPath \ "product").read[String] and
      (JsPath \ "type").read[String] and
      (JsPath \ "uri").read[String]
    )(PrivateUser.apply _)
}
