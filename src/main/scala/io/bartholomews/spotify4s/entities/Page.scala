package io.bartholomews.spotify4s.entities

import cats.effect.Sync
import fs2.Pipe
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.{Decoder, HCursor, Json}

/**
  * href 	string 	A link to the Web API endpoint returning the full result of the request.
  * items 	an array of objects 	The requested data.
  * limit 	integer 	The maximum number of items in the response (as set in the query or by default).
  * next 	string 	URL to the next page of items. ( null if none)
  * offset 	integer 	The offset of the items returned (as set in the query or by default).
  * previous 	string 	URL to the previous page of items. ( null if none)
  * total 	integer 	The maximum number of items available to return.
  *
  * @tparam A
  */
@ConfiguredJsonCodec(encodeOnly = true)
case class Page[A](
  href: String,
  items: List[A],
  limit: Option[Int],
  next: Option[String],
  offset: Option[Int],
  previous: Option[String],
  total: Int
)

object Page {
  def pipeDecoder[F[_]: Sync, A](
    implicit evidence: Sync[F],
    decode: Decoder[A]
  ): Pipe[F, Json, Page[A]] =
    io.circe.fs2.decoder[F, Page[A]](evidence, Page.decoder)

  implicit def decoder[A](implicit decode: Decoder[A]): Decoder[Page[A]] =
    (c: HCursor) =>
      for {
        href <- c.downField("href").as[String]
        items <- c.downField("items").as[Option[List[A]]]
        limit <- c.downField("limit").as[Option[Int]]
        next <- c.downField("next").as[Option[String]]
        offset <- c.downField("offset").as[Option[Int]]
        previous <- c.downField("previous").as[Option[String]]
        total <- c.downField("total").as[Int]
      } yield Page(href, items.getOrElse(List.empty), limit, next, offset, previous, total)
}
