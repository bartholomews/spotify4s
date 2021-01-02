package io.bartholomews.spotify4s.core.entities

import io.bartholomews.iso_country.CountryCodeAlpha2
import sttp.model.Uri

/**
  * https://developer.spotify.com/documentation/web-api/reference/object-model/#user-object-private
  *
  * @param country    The country of the user, as set in the user’s account profile.
  *                   An ISO 3166-1 alpha-2 country code.
  *                   This field is only available when the current user has granted access
  *                   to the user-read-private scope.
  *
  * @param displayName The name displayed on the user’s profile. None if not available.
  *
  * @param email        The user’s email address, as entered by the user when creating their account.
  *                    Important! This email address is unverified;
  *                    there is no proof that it actually belongs to the user.
  *                    This field is only available when the current user has granted access
  *                    to the user-read-email scope.
  *
  * @param externalUrls Known external URLs for this user.
  *
  * @param followers    Information about the followers of the user.
  *
  * @param href         A link to the Web API endpoint for this user.
  *
  * @param id           The Spotify user ID for the user
  *
  * @param images       The user’s profile image.
  *
  * @param product      The user’s Spotify subscription level: “premium”, “free”, etc.
  *                      (The subscription level “open” can be considered the same as “free”.)
  *                     This field is only available when the current user has granted access
  *                      to the user-read-private scope.
  *
  * @param uri  The Spotify URI for the user.
  */
case class PrivateUser(
  country: Option[CountryCodeAlpha2],
  displayName: Option[String],
  email: Option[String],
  externalUrls: ExternalResourceUrl,
  followers: Followers,
  href: Uri,
  id: SpotifyUserId,
  images: List[SpotifyImage],
  product: Option[SubscriptionLevel],
  uri: SpotifyUri
)
