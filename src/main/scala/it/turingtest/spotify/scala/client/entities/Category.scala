package it.turingtest.spotify.scala.client.entities

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

/**
  *
  */
case class Category(href: String, icons: Seq[Image], id: String, name: String)

object Category {
  implicit val categoryReads: Reads[Category] = (
    (JsPath \ "href").read[String] and
      (JsPath \ "icons").read[Seq[Image]] and
        (JsPath \ "id").read[String] and
        (JsPath \ "name").read[String]
    )(Category.apply _)
}
