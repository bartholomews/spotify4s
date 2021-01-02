package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.{
  ExternalResourceUrl,
  Followers,
  PublicUser,
  SpotifyImage,
  SpotifyUri,
  SpotifyUserId
}
import io.circe.{Decoder, HCursor}
import sttp.model.Uri

object CircePublicUser {
  implicit val decoder: Decoder[PublicUser] = (c: HCursor) =>
    for {
      displayName <- c.downField("display_name").as[Option[String]]
      externalUrls <- c.downField("external_urls").as[ExternalResourceUrl]
      followers <- c.downField("followers").as[Option[Followers]]
      href <- c.downField("href").as[Uri]
      id <- c.downField("id").as[SpotifyUserId]
      images <- c.downField("images").as[Option[List[SpotifyImage]]]
      uri <- c.downField("uri").as[SpotifyUri]
    } yield PublicUser(
      displayName,
      externalUrls,
      followers,
      href,
      id,
      images.getOrElse(List.empty),
      uri
    )
}
