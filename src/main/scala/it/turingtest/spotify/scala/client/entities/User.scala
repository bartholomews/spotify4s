package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

object UserReads {

  val publicUser = {
    (JsPath \ "display_name").readNullable[String] and
      (JsPath \ "external_urls").read[ExternalURL] and
      (JsPath \ "followers").readNullable[Followers] and
      (JsPath \ "href").read[String] and
      (JsPath \ "id").read[String] and
      ((JsPath \ "images").read[Seq[Image]] or Reads.pure(Seq.empty[Image])) and
      (JsPath \ "type").read[String] and
      (JsPath \ "uri").read[String]
  }

  val privateUser = {
    publicUser and
      (JsPath \ "birthdate").readNullable[String] and
      (JsPath \ "country").readNullable[String] and
      (JsPath \ "email").readNullable[String] and
      (JsPath \ "product").readNullable[String]
  }

}

/**
  *
  * @see https://developer.spotify.com/web-api/object-model/#user-object-public
  *
  * @param display_name  The name displayed on the user's profile. null if not available.
  *
  * @param external_urls Known external URLs for this user.
  *
  * @param followers     Information about the followers of the user.
  *
  * @param href          A link to the Web API endpoint for this user.
  *
  * @param id            The Spotify user ID for the user.
  *
  * @param images        The user's profile image.
  *
  * @param objectType    The object type: "user".
  *
  * @param uri           The Spotify URI for the user.
  *
  */
case class User
(
  display_name: Option[String],
  external_urls: ExternalURL,
  followers: Option[Followers],
  href: String,
  id: String,
  images: Seq[Image],
  objectType: String,
  uri: String
)

object User {
  implicit val userReads: Reads[User] = UserReads.publicUser(User.apply _)
}

/**
  *
  * @see https://developer.spotify.com/web-api/object-model/#user-object-private
  *
  * @param display_name  The name displayed on the user's profile. null if not available.
  *
  * @param external_urls Known external URLs for this user.
  *
  * @param followers     Information about the followers of the user.
  *
  * @param href          A link to the Web API endpoint for this user.
  *
  * @param id            The Spotify user ID for the user.
  *
  * @param images        The user's profile image.
  *
  * @param objectType    The object type: "user".
  *
  * @param uri           The Spotify URI for the user.
  *
  * @param birthdate     The user's date-of-birth.
  *                      This field is only available when the current user
  *                      has granted access to the `user-read-birthdate` scope.
  * @param country       The country of the user, as set in the user's account profile.
  *                      An ISO 3166-1 alpha-2 country code.
  *                      This field is only available when the current user
  *                      has granted access to the `user-read-private` scope.
  * @param email         The user's email address, as entered by the user when creating their account.
  *                      There is no proof that it actually belongs to the user.
  *                      This field is only available when the current user
  *                      has granted access to the `user-read-email` scope.
  * @param product       The user's Spotify subscription level: "premium", "free", etc.
  *                      (The subscription level "open" can be considered the same as "free".)
  *                      This field is only available when the current user
  *                      has granted access to the `user-read-private` scope.
  */
case class UserPrivate
(
  display_name: Option[String],
  external_urls: ExternalURL,
  followers: Option[Followers],
  href: String,
  id: String,
  images: Seq[Image],
  objectType: String,
  uri: String,
  birthdate: Option[String],
  country: Option[String],
  email: Option[String],
  product: Option[String]
)

object UserPrivate {
  implicit val userPrivateReads: Reads[UserPrivate] = UserReads.privateUser(UserPrivate.apply _)
}
