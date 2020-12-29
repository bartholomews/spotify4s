package io.bartholomews.spotify4s.entities

import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder
import sttp.model.Uri

/**
  * https://developer.spotify.com/documentation/web-api/reference/object-model/#followers-object
  * @param href   A link to the Web API endpoint providing full details of the followers;
  *                None if not available.
  *
  * @param total  The total number of followers.
  */
case class Followers(href: Option[Uri], total: Int)

object Followers {
  implicit val decoder: Decoder[Followers] = deriveConfiguredDecoder
}
