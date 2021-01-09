//package io.bartholomews.spotify4s.circe.entities
//
//import io.bartholomews.spotify4s.core.entities.PlaylistTrack
//import io.circe.Decoder
//import io.circe.generic.extras.semiauto.deriveConfiguredDecoder
//
///*
//  additional_types 	Optional.
//  A comma-separated list of item types that your client supports besides the default track type.
//  Valid types are: track and episode.
//  Note: This parameter was introduced to allow existing clients to maintain their current behaviour
//  and might be deprecated in the future. In addition to providing this parameter,
//  make sure that your client properly handles cases of new types in the future
//  by checking against the type field of each object.
// */
////sealed trait PlaylistTrackEntity
////
////object PlaylistTrackEntity {
////  implicit val decoder: Decoder[PlaylistTrackEntity] =
////    List[Decoder[PlaylistTrackEntity]](
////      Decoder[PlaylistMusicTrack].widen,
////      Decoder[PlaylistEpisodeTrack].widen
////    ).reduceLeft(_ or _)
////
////  implicit val encoder: Encoder[PlaylistTrackEntity] = Encoder.instance {
////    case episode @ PlaylistEpisodeTrack(_) => episode.asJson
////    case track @ PlaylistMusicTrack(_) => track.asJson
////  }
////}
////
////case class PlaylistEpisodeTrack(value: FullEpisode) extends PlaylistTrackEntity
////case class PlaylistMusicTrack(value: FullTrack) extends PlaylistTrackEntity
