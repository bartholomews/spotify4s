package io.bartholomews.spotify4s.entities

import io.circe.generic.extras.ConfiguredJsonCodec
import org.http4s.Uri

/**
  * https://developer.spotify.com/documentation/web-api/reference/object-model/#followers-object
  * @param href   A link to the Web API endpoint providing full details of the followers;
  *                None if not available.
  *
  * @param total  The total number of followers.
  */
@ConfiguredJsonCodec
case class Followers(href: Option[Uri], total: Int)
