package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

/**
  * https://developer.spotify.com/web-api/object-model/#external-id-object
 *
  * @param isrc International Standard Recording Code
  * @param ean International Article Number
  * @param upc Universal Product Code
  */
case class ExternalID(isrc: Option[String], ean: Option[String], upc: Option[String]) {
  val value: Option[(String, String)]  = {
    if(isrc.isDefined) Some("isrc", isrc.get)
    else if(ean.isDefined) Some("ean", ean.get)
    else if(upc.isDefined) Some("upc", upc.get)
    else None
  }
}

object ExternalID {
  implicit val externalIDReads: Reads[ExternalID] = (
    (JsPath \ "isrc").readNullable[String] and
      (JsPath \ "ean").readNullable[String] and
      (JsPath \ "upc").readNullable[String]
    )(ExternalID.apply _)

}
