package io.bartholomews.spotify4s

import io.circe.generic.extras.Configuration
import io.circe.{Codec, Encoder}
import org.http4s.Uri

package object entities {
  def dropNullValues[A](encoder: Encoder[A]): Encoder[A] = encoder.mapJson(_.dropNullValues)
  implicit val defaultConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val uriCodec: Codec[Uri] = {
    import org.http4s.circe.{decodeUri, encodeUri}
    Codec.from(decodeUri, encodeUri)
  }
}
