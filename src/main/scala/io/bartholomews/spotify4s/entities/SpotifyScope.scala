package io.bartholomews.spotify4s.entities

import enumeratum.EnumEntry.Hyphencase
import enumeratum._
import io.bartholomews.fsclient.entities.oauth.Scope
import io.bartholomews.fsclient.utils.FsLogger

// https://developer.spotify.com/documentation/general/guides/scopes/
sealed trait SpotifyScope extends EnumEntry with Hyphencase

case object SpotifyScope extends Enum[SpotifyScope] with CirceEnum[SpotifyScope] {
  case object UGC_IMAGE_UPLOAD extends SpotifyScope
  case object USER_READ_PLAYBACK_STATE extends SpotifyScope
  case object STREAMING extends SpotifyScope
  case object USER_READ_EMAIL extends SpotifyScope
  case object PLAYLIST_READ_COLLABORATIVE extends SpotifyScope
  case object USER_MODIFY_PLAYBACK_STATE extends SpotifyScope
  case object USER_READ_PRIVATE extends SpotifyScope
  case object PLAYLIST_MODIFY_PUBLIC extends SpotifyScope
  case object USER_LIBRARY_MODIFY extends SpotifyScope
  case object USER_TOP_READ extends SpotifyScope
  case object USER_READ_PLAYBACK_POSITION extends SpotifyScope
  case object USER_READ_CURRENTLY_PLAYING extends SpotifyScope
  case object PLAYLIST_READ_PRIVATE extends SpotifyScope
  case object USER_FOLLOW_READ extends SpotifyScope
  case object APP_REMOTE_CONTROL extends SpotifyScope
  case object USER_READ_RECENTLY_PLAYED extends SpotifyScope
  case object PLAYLIST_MODIFY_PRIVATE extends SpotifyScope
  case object USER_FOLLOW_MODIFY extends SpotifyScope
  case object USER_LIBRARY_READ extends SpotifyScope

  override val values: IndexedSeq[SpotifyScope] = findValues

  def fromScope(scope: Scope): List[SpotifyScope] = {
    scope.values
      .foldRight[(List[String], List[SpotifyScope])]((List.empty, List.empty))({
        case (str, (errors, scopes)) =>
          SpotifyScope
            .withNameOption(str)
            .fold(Tuple2(str :: errors, scopes))(scope => Tuple2(errors, scope :: scopes))
      }) match {
      case (errors, scopes) =>
        FsLogger.logger.warn("unknown scopes returned", errors)
        scopes
    }
  }
}
