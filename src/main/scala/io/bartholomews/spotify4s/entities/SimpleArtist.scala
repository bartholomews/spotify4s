package io.bartholomews.spotify4s.entities

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder, HCursor}

// https://developer.spotify.com/documentation/web-api/reference/object-model/#artist-object-simplified
case class SimpleArtist(
  externalUrls: Option[ExternalResourceUrl],
  href: Option[String],
  id: Option[SpotifyId],
  name: String,
  uri: Option[SpotifyUri]
)

object SimpleArtist {
  implicit val encoder: Encoder[SimpleArtist] = deriveEncoder[SimpleArtist]
  implicit val decoder: Decoder[SimpleArtist] = (c: HCursor) =>
    for {
      externalUrls <- Right(c.downField("external_urls").as[ExternalResourceUrl].toOption)
      href <- c.downField("href").as[Option[String]]
      id <- c.downField("id").as[Option[SpotifyId]]
      name <- c.downField("name").as[String]
      uri <- c.downField("uri").as[Option[SpotifyUri]]
    } yield SimpleArtist(externalUrls, href, id, name, uri)
}
