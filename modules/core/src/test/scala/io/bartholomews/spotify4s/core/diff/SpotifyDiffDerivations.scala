package io.bartholomews.spotify4s.core.diff

import java.time.Month

import com.softwaremill.diffx.Diff
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.scalatestudo.diff.DiffDerivations
import io.bartholomews.spotify4s.core.entities.TimeInterval.Bar
import io.bartholomews.spotify4s.core.entities._

trait SpotifyDiffDerivations extends DiffDerivations {
  implicit def pageDiff[A](implicit diff: Diff[A]): Diff[Page[A]] = Diff.derived[Page[A]]
  implicit val audioFeaturesDiff: Diff[AudioFeatures] = Diff.derived[AudioFeatures]
  implicit val audioKeyDiff: Diff[AudioKey] = Diff.derived[AudioKey]
  implicit val audioModeDiff: Diff[AudioMode] = Diff.derived[AudioMode]
  implicit val audioSectionDiff: Diff[AudioSection] = Diff.derived[AudioSection]
  implicit val albumTypeDiff: Diff[AlbumType] = Diff.derived[AlbumType]
  implicit val barDiff: Diff[Bar] = Diff.derived[Bar]
  implicit val confidenceDiff: Diff[Confidence] = Diff.derived[Confidence]
  implicit val collectionLinkDiff: Diff[CollectionLink] = Diff.derived[CollectionLink]
  implicit val copyrightDiff: Diff[Copyright] = Diff.derived[Copyright]
  implicit val countryCodeAlpha2Diff: Diff[CountryCodeAlpha2] = Diff.derived[CountryCodeAlpha2]
  implicit val externalIdsDiff: Diff[ExternalIds] = Diff.derived[ExternalIds]
  implicit val externalResourceUrlDiff: Diff[ExternalResourceUrl] = Diff.derived[ExternalResourceUrl]
  implicit val followersDiff: Diff[Followers] = Diff.derived[Followers]
  implicit val fullAlbumDiff: Diff[FullAlbum] = Diff.derived[FullAlbum]
  implicit val linkedTrackDiff: Diff[LinkedTrack] = Diff.derived[LinkedTrack]
  implicit val modalityDiff: Diff[Modality] = Diff.derived[Modality]
  implicit val pitchClassDiff: Diff[PitchClass] = Diff.derived[PitchClass]
  implicit val publicUserDiff: Diff[PublicUser] = Diff.derived[PublicUser]
  implicit val releaseDateDiff: Diff[ReleaseDate] = Diff.derived[ReleaseDate]
  implicit val restrictionsDiff: Diff[Restrictions] = Diff.derived[Restrictions]
  implicit val simpleArtistDiff: Diff[SimpleArtist] = Diff.derived[SimpleArtist]
  implicit val simpleTrackDiff: Diff[SimpleTrack] = Diff.derived[SimpleTrack]
  implicit val simplePlaylistDiff: Diff[SimplePlaylist] = Diff.derived[SimplePlaylist]
  implicit val spotifyIdDiff: Diff[SpotifyId] = Diff.derived[SpotifyId]
  implicit val spotifyImageDiff: Diff[SpotifyImage] = Diff.derived[SpotifyImage]
  implicit val spotifyUriDiff: Diff[SpotifyUri] = Diff.derived[SpotifyUri]
  implicit val spotifyUserIdDiff: Diff[SpotifyUserId] = Diff.derived[SpotifyUserId]
  implicit val tempoDiff: Diff[Tempo] = Diff.derived[Tempo]
  implicit val timeSignatureDiff: Diff[TimeSignature] = Diff.derived[TimeSignature]

  // TODO fallback ?
  implicit val monthDiff: Diff[Month] = Diff.derived[Month]
}
