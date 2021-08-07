package io.bartholomews.spotify4s.core.entities

import io.bartholomews.spotify4s.core.entities.SpotifyId.SpotifyUserId
import sttp.model.Uri

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
case class PublicUser(
  displayName: Option[String],
  externalUrls: ExternalResourceUrl,
  followers: Option[Followers],
  href: Uri,
  id: SpotifyUserId,
  images: List[SpotifyImage],
  uri: SpotifyUri
)
