package it.turingtest.spotify.scala.client.utils

import java.nio.charset.StandardCharsets
import java.util.Base64

import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat

object ConversionUtils {

  def base64(secret: String): String = {
    Base64.getEncoder.encodeToString(secret.getBytes(StandardCharsets.UTF_8))
  }

  def seq(query: (String, Option[Any])*): Seq[(String, String)] = {
    query.flatMap { case (key, value) => value match {

      case Some(datetime: LocalDateTime) =>
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")
        Option(key, datetime.toString(formatter))

      case _ => value.map(v => (key, v.toString)) }
    }
  }

}
