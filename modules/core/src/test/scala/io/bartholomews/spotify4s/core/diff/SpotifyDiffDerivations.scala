package io.bartholomews.spotify4s.core.diff

import com.softwaremill.diffx.{Derived, Diff}
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.scalatestudo.diff.DiffDerivations
import io.bartholomews.spotify4s.core.entities.ExternalResourceUrl.SpotifyResourceUrl
import io.bartholomews.spotify4s.core.entities.SpotifyId.{
  SpotifyAlbumId,
  SpotifyArtistId,
  SpotifyPlaylistId,
  SpotifyPlaylistName,
  SpotifyUserId
}
import io.bartholomews.spotify4s.core.entities.TimeInterval.Bar
import io.bartholomews.spotify4s.core.entities._

import java.time.Month

trait SpotifyDiffDerivations extends DiffDerivations {
  implicit def pageDiff[A](implicit diff: Diff[A]): Diff[Page[A]] = Diff.derived[Page[A]]
  implicit val monthDiff: Diff[Month] = Diff.diffForNumeric[Int].contramap[Month](_.getValue)
  implicit val countryCodeAlpha2Diff: Diff[CountryCodeAlpha2] =
    Diff.diffForString.contramap[CountryCodeAlpha2](_.value)

  implicit val spotifyUriDiff: Diff[SpotifyUri] = Diff.derived[SpotifyUri]
  implicit val spotifyIdDiff: Diff[SpotifyId] = Diff.derived[SpotifyId]
  implicit val spotifyArtistIdDiff: Diff[SpotifyArtistId] = Diff.derived[SpotifyArtistId]
  implicit val spotifyAlbumIdDiff: Diff[SpotifyAlbumId] = Diff.derived[SpotifyAlbumId]
  implicit val spotifyUserIdDiff: Diff[SpotifyUserId] = Diff.derived[SpotifyUserId]
  implicit val spotifyGenreDiff: Diff[SpotifyGenre] = Diff.derived[SpotifyGenre]
  implicit val spotifyPlaylistIdDiff: Diff[SpotifyPlaylistId] = Diff.derived[SpotifyPlaylistId]
  implicit val spotifyImageDiff: Diff[SpotifyImage] = Diff.derived[SpotifyImage]
  implicit val copyrightDiff: Diff[Copyright] = Diff.derived[Copyright]
  implicit val restrictionsDiff: Diff[Restrictions] = Diff.derived[Restrictions]

  // TODO: https://github.com/softwaremill/diffx/issues/218
  implicit val spotifyResourceUrlDiff: Derived[Diff[SpotifyResourceUrl]] =
    Derived(Diff.derived[SpotifyResourceUrl])
  implicit val externalResourceUrlDiff: Derived[Diff[ExternalResourceUrl]] =
    Derived(Diff.derived[ExternalResourceUrl])
  // TODO: https://github.com/softwaremill/diffx/issues/218
  implicit val externalIdsDiff: Derived[Diff[ExternalIds]] = Derived(Diff.derived[ExternalIds])

  implicit val collectionLinkDiff: Diff[CollectionLink] = Diff.derived[CollectionLink]
  implicit val followersDiff: Diff[Followers] = Diff.derived[Followers]
  implicit val linkedTrackDiff: Diff[LinkedTrack] = Diff.derived[LinkedTrack]
  implicit val releaseDateDiff: Diff[ReleaseDate] = Diff.derived[ReleaseDate]
  implicit val simpleArtistDiff: Diff[SimpleArtist] = Diff.derived[SimpleArtist]
  implicit val simpleTrackDiff: Diff[SimpleTrack] = Diff.derived[SimpleTrack]
  implicit val publicUserDiff: Diff[PublicUser] = Diff.derived[PublicUser]
  implicit val spotifyPlaylistNameDiff: Diff[SpotifyPlaylistName] = Diff.derived[SpotifyPlaylistName]
  implicit val simplePlaylistDiff: Diff[SimplePlaylist] = Diff.derived[SimplePlaylist]

  implicit val albumTypeDiff: Diff[AlbumType] = Diff.diffForString.contramap[AlbumType](_.entryName)
  implicit val fullAlbumDiff: Diff[FullAlbum] = Diff.derived[FullAlbum]

  implicit val confidenceDiff: Diff[Confidence] = Diff.derived[Confidence]
  implicit val barDiff: Diff[Bar] = Diff.derived[Bar]
  implicit val tempoDiff: Diff[Tempo] = Diff.derived[Tempo]
  implicit val modalityDiff: Diff[Modality] = Diff.diffForNumeric[Int].contramap[Modality](_.value)
  implicit val pitchClassDiff: Diff[PitchClass] = Diff.derived[PitchClass]
  implicit val audioKeyDiff: Diff[AudioKey] = Diff.derived[AudioKey]
  implicit val audioModeDiff: Diff[AudioMode] = Diff.derived[AudioMode]
  implicit val timeSignatureDiff: Diff[TimeSignature] = Diff.derived[TimeSignature]
  implicit val audioSectionDiff: Diff[AudioSection] = Diff.derived[AudioSection]
  implicit val audioFeaturesDiff: Diff[AudioFeatures] = Diff.derived[AudioFeatures]
}
