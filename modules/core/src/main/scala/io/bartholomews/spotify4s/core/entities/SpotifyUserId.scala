package io.bartholomews.spotify4s.core.entities

import cats.Order
import cats.data.NonEmptyList
import eu.timepit.refined.api.Validate
import eu.timepit.refined.api.Validate.Plain
import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.predicates.all.Size
import eu.timepit.refined.predicates.collection.MaxSize
import eu.timepit.refined.refineV
import io.bartholomews.spotify4s.core.api.SpotifyApi.SpotifyUris
import io.bartholomews.spotify4s.core.validators.RefinedValidators.{maxSizeP, NelMaxSizeValidators}
import shapeless.Nat._0
import shapeless.Witness
import sttp.model.Uri
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
final case class SpotifyUri(value: String) extends AnyVal
object SpotifyUri extends NelMaxSizeValidators[SpotifyUri, SpotifyUris](maxSize = 100) {
  private def validateSpotifyUris: Plain[NonEmptyList[SpotifyUri], MaxSize[100]] = {
    Validate
      .fromPredicate(
        (d: NonEmptyList[SpotifyUri]) => d.length <= 100,
        (_: NonEmptyList[SpotifyUri]) => "a maximum of 100 uris can be set in one request",
        Size[Interval.Closed[_0, Witness.`100`.T]](maxSizeP)
      )
  }

  override def fromNel(xs: NonEmptyList[SpotifyUri]): Either[String, SpotifyUris] =
    refineV[MaxSize[100]](xs)(validateSpotifyUris)
}

/**
  * @param value The base-62 identifier that you can find at the end of the Spotify URI (see above)
  *              for an artist, track, album, playlist, etc.
  *              Unlike a Spotify URI, a Spotify ID does not clearly identify the type of resource;
  *              that information is provided elsewhere in the call.
  *              example: "6rqhFgbbKwnb9MLmUQDhG6"
  */
final case class SpotifyId(value: String) extends AnyVal
object SpotifyId {
  implicit val order: Order[SpotifyId] = (x: SpotifyId, y: SpotifyId) => x.value.compareTo(y.value)

  final case class SpotifyArtistId(value: String) extends AnyVal

  /**
    * @param value The unique string identifying the Spotify user
    *              that you can find at the end of the Spotify URI for the user.
    *              The ID of the current user can be obtained via the Web API endpoint.
    *              example: "wizzler"
    */
  final case class SpotifyUserId(value: String) extends AnyVal
  final case class SpotifyAlbumId(value: String) extends AnyVal
  final case class SpotifyPlaylistId(value: String) extends AnyVal
  final case class SpotifyTrackId(value: String) extends AnyVal
  object SpotifyTrackId {
    implicit val ordering: Ordering[SpotifyTrackId] = Ordering.String.on(_.value)
    implicit val order: Order[SpotifyTrackId] = Order.fromOrdering
  }
}

/**
  * @param value The unique string identifying the Spotify category.
  *               example: "party"
  */
final case class SpotifyCategoryId(value: String) extends AnyVal

/**
  * @param value  An HTML link that opens a track, album, app, playlist or other Spotify resource in a Spotify client
  *               (which client is determined by the user’s device and account settings at play.spotify.com).
  *               example: "http://open.spotify.com/track/6rqhFgbbKwnb9MLmUQDhG6"
  */
final case class SpotifyUrl(value: Uri) extends AnyVal

final case class SpotifyGenre(value: String) extends AnyVal
