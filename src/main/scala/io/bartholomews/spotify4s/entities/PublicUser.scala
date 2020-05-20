package io.bartholomews.spotify4s.entities

import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.{Decoder, HCursor}
import org.http4s.Uri

/**
  * https://developer.spotify.com/documentation/web-api/reference/object-model/#user-object-public
  * @param displayName  The name displayed on the user’s profile. None if not available.
  * @param externalUrls Known public external URLs for this user.
  * @param followers  Information about the followers of this user.
  * @param href A link to the Web API endpoint for this user.
  * @param id The Spotify user ID for this user.
  * @param images The user’s profile image.
  * @param uri  The Spotify URI for this user.
  */
@ConfiguredJsonCodec(encodeOnly = true)
case class PublicUser(
  displayName: Option[String],
  externalUrls: ExternalResourceUrl,
  followers: Option[Followers],
  href: Uri,
  id: SpotifyUserId,
  images: List[SpotifyImage],
  uri: SpotifyUri
)

object PublicUser {
  implicit val decoder: Decoder[PublicUser] = (c: HCursor) =>
    for {
      displayName <- c.downField("display_name").as[Option[String]]
      externalUrls <- c.downField("external_urls").as[ExternalResourceUrl]
      followers <- c.downField("followers").as[Option[Followers]]
      href <- c.downField("href").as[Uri]
      id <- c.downField("id").as[SpotifyUserId]
      images <- c.downField("images").as[Option[List[SpotifyImage]]]
      uri <- c.downField("uri").as[SpotifyUri]
    } yield
      PublicUser(
        displayName,
        externalUrls,
        followers,
        href,
        id,
        images.getOrElse(List.empty),
        uri
    )
}
