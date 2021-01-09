package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.{ExternalResourceUrl, SimpleArtist, SpotifyId, SpotifyUri}
import io.circe.{Decoder, HCursor}

private[spotify4s] object SimpleArtistCirce {
  import codecs._
  val decoder: Decoder[SimpleArtist] = (c: HCursor) =>
    for {
      externalUrls <- Right(c.downField("external_urls").as[ExternalResourceUrl].toOption)
      href <- c.downField("href").as[Option[String]]
      id <- c.downField("id").as[Option[SpotifyId]]
      name <- c.downField("name").as[String]
      uri <- c.downField("uri").as[Option[SpotifyUri]]
    } yield SimpleArtist(externalUrls, href, id, name, uri)
}
