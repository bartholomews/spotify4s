package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

/**
  * @see https://developer.spotify.com/web-api/object-model/#user-object-private
  */

/**
  * @param birthdate     The user's date-of-birth.
  *                      This field is only available when the current user
  *                      has granted access to the `user-read-birthdate` scope.
  * @param country       The country of the user, as set in the user's account profile.
  *                      An ISO 3166-1 alpha-2 country code.
  *                      This field is only available when the current user
  *                      has granted access to the `user-read-private` scope.
  * @param display_name  The name displayed on the user's profile. null if not available.
  * @param email         The user's email address, as entered by the user when creating their account.
  *                      There is no proof that it actually belongs to the user.
  *                      This field is only available when the current user
  *                      has granted access to the `user-read-email` scope.
  * @param external_urls Known external URLs for this user.
  * @param followers     Information about the followers of the user.
  * @param href          A link to the Web API endpoint for this user.
  * @param id            The Spotify user ID for the user.
  * @param images        The user's profile image.
  * @param product       The user's Spotify subscription level: "premium", "free", etc.
  *                      (The subscription level "open" can be considered the same as "free".)
  *                      This field is only available when the current user
  *                      has granted access to the `user-read-private` scope.
  * @param objectType    The object type: "user".
  * @param uri           The Spotify URI for the user.
  */
case class UserPrivate
(
  birthdate: Option[String],
  country: Option[String],
  display_name: Option[String],
  email: Option[String],
  external_urls: ExternalURL,
  followers: Followers,
  href: String,
  id: String,
  images: List[Image],
  product: Option[String],
  objectType: String,
  uri: String
)

object UserPrivate {
  implicit val userPrivateReads: Reads[UserPrivate] = (
    (JsPath \ "birthdate").readNullable[String] and
      (JsPath \ "country").readNullable[String] and
      (JsPath \ "display_name").readNullable[String] and
      (JsPath \ "email").readNullable[String] and
      (JsPath \ "external_urls").read[ExternalURL] and
      (JsPath \ "followers").read[Followers] and
      (JsPath \ "href").read[String] and
      (JsPath \ "id").read[String] and
      (JsPath \ "images").read[List[Image]] and
      (JsPath \ "product").readNullable[String] and
      (JsPath \ "type").read[String] and
      (JsPath \ "uri").read[String]
    )(UserPrivate.apply _)
}
