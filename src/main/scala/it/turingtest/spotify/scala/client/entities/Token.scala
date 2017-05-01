package it.turingtest.spotify.scala.client.entities

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

case class Token(access_token: String, token_type: String, scope: Option[String],
                 expires_in: Int, refresh_token: Option[String]) {

  val createdAt: Long = System.currentTimeMillis()
  def expired: Boolean = System.currentTimeMillis() > (createdAt + expires_in)
}

object Token {
  implicit val tokenReads: Reads[Token] = (
    (JsPath \ "access_token").read[String] and
      (JsPath \ "token_type").read[String] and
      (JsPath \ "scope").readNullable[String] and
      (JsPath \ "expires_in").read[Int] and
      (JsPath \ "refresh_token").readNullable[String]
    ) (Token.apply _)
}
