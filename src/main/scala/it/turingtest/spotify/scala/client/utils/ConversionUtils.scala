package it.turingtest.spotify.scala.client.utils

import java.nio.charset.StandardCharsets
import java.util.{Base64, Locale}

import com.vitorsvieira.iso.ISOCountry.ISOCountry
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat

object ConversionUtils {

  def base64(secret: String): String = {
    Base64.getEncoder.encodeToString(secret.getBytes(StandardCharsets.UTF_8))
  }

  def seq(query: (String, Option[Any])*): Seq[(String, String)] = {

    query.flatMap { case (key, value) => value match {

      case None => None

      case Some(datetime: LocalDateTime) =>
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")
        Option(key, datetime.toString(formatter))

      case Some(locale: Locale) => Option(key, s"${locale.getLanguage}_${locale.getCountry}")

      case Some(country: ISOCountry) => Option(key, country.value)

      case _ => value.map(v => (key, v.toString)) }

    }
  }

}
