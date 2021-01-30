package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.Page
import io.circe.{Decoder, HCursor}
import sttp.model.Uri

private[spotify4s] object PageCirce {
  import io.bartholomews.fsclient.circe.codecs.sttpUriCodec
  def decoder[A](implicit decode: Decoder[A]): Decoder[Page[A]] =
    (c: HCursor) =>
      for {
        href <- c.downField("href").as[Uri]
        items <- c.downField("items").as[Option[List[A]]]
        limit <- c.downField("limit").as[Option[Int]]
        next <- c.downField("next").as[Option[String]]
        offset <- c.downField("offset").as[Option[Int]]
        previous <- c.downField("previous").as[Option[String]]
        total <- c.downField("total").as[Int]
      } yield Page(href, items.getOrElse(List.empty), limit, next, offset, previous, total)
}
