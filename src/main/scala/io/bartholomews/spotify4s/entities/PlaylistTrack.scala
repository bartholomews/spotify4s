package io.bartholomews.spotify4s.entities

import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder

/**
  * https://developer.spotify.com/documentation/web-api/reference/playlists/get-playlists-tracks/
  *
  * @param addedAt  The date and time the track was added.
  *                 Note that some very old playlists may return None in this field.
  *
  * @param addedBy  The Spotify user who added the track.
  *                 Note that some very old playlists may return None in this field.
  *
  * @param isLocal  Whether this track is a local file or not.
  *                 (see https://developer.spotify.com/documentation/general/guides/local-files-spotify-playlists/)
  *
  * @param track  Information about the track.
  */
case class PlaylistTrack(
  addedAt: Option[String], // TODO: Timestamps are returned in ISO 8601 format as Coordinated Universal Time (UTC) with a zero offset: YYYY-MM-DDTHH:MM:SSZ. If the time is imprecise (for example, the date/time of an album release), an additional field indicates the precision; see for example, release_date in an album object.
  addedBy: Option[PublicUser],
  isLocal: Boolean,
  track: FullTrack
)

object PlaylistTrack {
  implicit val decoder: Decoder[PlaylistTrack] = deriveConfiguredDecoder
}

/*
  additional_types 	Optional.
  A comma-separated list of item types that your client supports besides the default track type.
  Valid types are: track and episode.
  Note: This parameter was introduced to allow existing clients to maintain their current behaviour
  and might be deprecated in the future. In addition to providing this parameter,
  make sure that your client properly handles cases of new types in the future
  by checking against the type field of each object.
 */
//sealed trait PlaylistTrackEntity
//
//object PlaylistTrackEntity {
//  implicit val decoder: Decoder[PlaylistTrackEntity] =
//    List[Decoder[PlaylistTrackEntity]](
//      Decoder[PlaylistMusicTrack].widen,
//      Decoder[PlaylistEpisodeTrack].widen
//    ).reduceLeft(_ or _)
//
//  implicit val encoder: Encoder[PlaylistTrackEntity] = Encoder.instance {
//    case episode @ PlaylistEpisodeTrack(_) => episode.asJson
//    case track @ PlaylistMusicTrack(_) => track.asJson
//  }
//}
//
//case class PlaylistEpisodeTrack(value: FullEpisode) extends PlaylistTrackEntity
//case class PlaylistMusicTrack(value: FullTrack) extends PlaylistTrackEntity
