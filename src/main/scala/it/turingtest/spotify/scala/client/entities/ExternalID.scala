package it.turingtest.spotify.scala.client.entities

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

/**
  * https://developer.spotify.com/web-api/object-model/#external-id-object
 *
  * @param isrc International Standard Recording Code
  * @param ean International Article Number
  * @param upc Universal Product Code
  */
case class ExternalID(private val isrc: Option[String],
                      private val ean: Option[String],
                      private val upc: Option[String]) {

  def isISRC: Boolean = isrc.isDefined
  def isEAN: Boolean = ean.isDefined
  def isUPC: Boolean = upc.isDefined

  val value: Option[ExID]  = {
    if(isISRC) Some(ISRC(isrc.get))
    else if(isEAN) Some(EAN(ean.get))
    else if(isUPC) Some(UPC(upc.get))
    else None
  }

}

sealed trait ExID { val value: String }
case class ISRC(value: String) extends ExID
case class EAN(value: String) extends ExID
case class UPC(value: String) extends ExID

object ExternalID {
  implicit val externalIDReads: Reads[ExternalID] = (
    (JsPath \ "isrc").readNullable[String] and
      (JsPath \ "ean").readNullable[String] and
      (JsPath \ "upc").readNullable[String]
    )(ExternalID.apply _)

}
