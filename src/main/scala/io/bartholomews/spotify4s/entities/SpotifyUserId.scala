package io.bartholomews.spotify4s.entities

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec
import org.http4s.Uri
/*
  https://developer.spotify.com/documentation/web-api/#spotify-uris-and-ids
 */

/**
  * @param value  The resource identifier that you can enter,
  *                for example, in the Spotify Desktop client’s search box to locate an artist, album, or track.
  *                To find a Spotify URI simply right-click (on Windows) or Ctrl-Click (on a Mac)
  *                on the artist’s or album’s or track’s name.
  *                example: "spotify:track:6rqhFgbbKwnb9MLmUQDhG6"
  */
case class SpotifyUri(value: String) extends AnyVal
object SpotifyUri {
  implicit val codec: Codec[SpotifyUri] = deriveUnwrappedCodec
}

/**
  * @param value The base-62 identifier that you can find at the end of the Spotify URI (see above)
  *              for an artist, track, album, playlist, etc.
  *              Unlike a Spotify URI, a Spotify ID does not clearly identify the type of resource;
  *              that information is provided elsewhere in the call.
  *              example: "6rqhFgbbKwnb9MLmUQDhG6"
  */
case class SpotifyId(value: String) extends AnyVal
object SpotifyId {
  implicit val codec: Codec[SpotifyId] = deriveUnwrappedCodec
}

/**
  * @param value The unique string identifying the Spotify category.
  *               example: "party"
  */
case class SpotifyCategoryId(value: String) extends AnyVal
object SpotifyCategoryId {
  implicit val codec: Codec[SpotifyCategoryId] = deriveUnwrappedCodec
}

/**
  * @param value The unique string identifying the Spotify user
  *              that you can find at the end of the Spotify URI for the user.
  *              The ID of the current user can be obtained via the Web API endpoint.
  *              example: "wizzler"
  */
case class SpotifyUserId(value: String) extends AnyVal
object SpotifyUserId {
  implicit val codec: Codec[SpotifyUserId] = deriveUnwrappedCodec
}

/**
  * @param value  An HTML link that opens a track, album, app, playlist or other Spotify resource in a Spotify client
  *               (which client is determined by the user’s device and account settings at play.spotify.com).
  *               example: "http://open.spotify.com/track/6rqhFgbbKwnb9MLmUQDhG6"
  */
case class SpotifyUrl(value: Uri) extends AnyVal
object SpotifyUrl {
  import org.http4s.circe.decodeUri
  implicit val codec: Codec[SpotifyUrl] = deriveUnwrappedCodec
}
