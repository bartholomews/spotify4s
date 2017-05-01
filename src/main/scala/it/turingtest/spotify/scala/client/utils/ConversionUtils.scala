package it.turingtest.spotify.scala.client.utils

import java.nio.charset.StandardCharsets
import java.util.Base64

object ConversionUtils {

  def base64(secret: String): String = {
    Base64.getEncoder.encodeToString(secret.getBytes(StandardCharsets.UTF_8))
  }

}
