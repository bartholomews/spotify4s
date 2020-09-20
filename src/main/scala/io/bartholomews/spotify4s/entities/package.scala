package io.bartholomews.spotify4s

import io.circe.Codec
import io.circe.generic.extras.Configuration
import org.http4s.Uri

package object entities {
  implicit val defaultConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val uriCodec: Codec[Uri] = {
    import org.http4s.circe.{decodeUri, encodeUri}
    Codec.from(decodeUri, encodeUri)
  }
}
