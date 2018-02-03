package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class Page[T]
(
href: String,
limit: Int,
next: Option[String],
offset: Int,
previous: Option[String],
total: Int,
items: Seq[T]
)

object Page {

  private val jsPage = {
    (JsPath \ "href").read[String] and
      (JsPath \ "limit").read[Int] and
      (JsPath \ "next").readNullable[String] and
      (JsPath \ "offset").read[Int] and
      (JsPath \ "previous").readNullable[String] and
      (JsPath \ "total").read[Int]
  }

  implicit def reads[A](implicit aReads: Reads[A]): Reads[Page[A]] = (
    jsPage and (JsPath \ "items").lazyRead(Reads.seq[A](aReads))
  )(Page.apply[A] _)

}

