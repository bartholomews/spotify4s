package io.bartholomews.spotify4s.circe

import io.bartholomews.fsclient.circe.FsClientCirceApi
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities.SpotifyId.{SpotifyAlbumId, SpotifyArtistId, SpotifyPlaylistId, SpotifyPlaylistName, SpotifyTrackId, SpotifyUserId}
import io.bartholomews.spotify4s.core.entities.TimeInterval.{Bar, Beat, Tatum}
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.core.entities.requests.{AddTracksToPlaylistRequest, CreatePlaylistRequest, ModifyPlaylistRequest}
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto._
import io.circe.{Codec, Decoder, Encoder}

object codecs extends SpotifyCirceApi

trait SpotifyCirceApi extends FsClientCirceApi {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val albumGroupCodec: Codec[AlbumGroup] = AlbumGroupCirce.codec
  implicit val albumTypeCodec: Codec[AlbumType] = AlbumTypeCirce.codec
  implicit val audioAnalysisCodec: Codec[AudioAnalysis] = deriveConfiguredCodec
  implicit val audioFeaturesCodec: Codec[AudioFeatures] = deriveConfiguredCodec
  implicit val audioFeaturesResponseCodec: Codec[AudioFeaturesResponse] = deriveConfiguredCodec
  implicit val audioSectionCodec: Codec[AudioSection] = AudioSectionCirce.codec
  implicit val audioSegmentCodec: Codec[AudioSegment] = AudioSegmentCirce.codec
  implicit val collectionLinkCodec: Codec[CollectionLink] = deriveConfiguredCodec
  implicit val confidenceCodec: Codec[Confidence] = deriveUnwrappedCodec
  implicit val countryCodeAlpha2Codec: Codec[CountryCodeAlpha2] = CountryCodeAlpha2Circe.codec

  private[spotify4s] val optionalCountryCodeList: Decoder[List[CountryCodeAlpha2]] =
    Decoder
      .decodeOption(Decoder.decodeList(countryCodeAlpha2Codec))
      .map(_.getOrElse(List.empty))

  implicit val copyrightCodec: Codec[Copyright] = deriveConfiguredCodec
  implicit val externalIdsCodec: Codec[ExternalIds] = ExternalIdsCirce.codec
  implicit val externalResourceUrlCodec: Codec[ExternalResourceUrl] = ExternalResourceUrlCirce.codec
  implicit val followersCodec: Codec[Followers] = deriveConfiguredCodec
  implicit val fullPlaylistCodec: Codec[FullPlaylist] = deriveConfiguredCodec
  implicit val fullTrackCodec: Codec[FullTrack] = Codec.from(FullTrackCirce.decoder, deriveConfiguredEncoder)
  implicit val fullTracksResponseCodec: Codec[FullTracksResponse] = deriveConfiguredCodec
  implicit val linkedTrackCodec: Codec[LinkedTrack] = deriveConfiguredCodec
  implicit val modalityCodec: Codec[Modality] = ModalityCirce.codec
  implicit val newReleasesCodec: Codec[NewReleases] = deriveConfiguredCodec

  implicit def pageEncoder[A](implicit encoder: Encoder[A]): Encoder[Page[A]] = deriveConfiguredEncoder
  implicit def pageDecoder[A](implicit decoder: Decoder[A]): Decoder[Page[A]] = PageCirce.decoder
  implicit def cursorEncoder[Id](implicit encoder: Encoder[Id]): Encoder[Cursor[Id]] = deriveConfiguredEncoder
  implicit def cursorDecoder[Id](implicit decoder: Decoder[Id]): Decoder[Cursor[Id]] = deriveConfiguredDecoder

  implicit def cursorPageEncoder[Id, A](
    implicit idCodec: Encoder[Id],
    entityCodec: Encoder[A]
  ): Encoder[CursorPage[Id, A]] = deriveConfiguredEncoder

  implicit def cursorPageDecoder[Id, A](
    implicit idCodec: Decoder[Id],
    entityCodec: Decoder[A]
  ): Decoder[CursorPage[Id, A]] = deriveConfiguredDecoder

  implicit val pitchClassCodec: Codec[PitchClass] = deriveUnwrappedCodec
  implicit val playlistTrackCodec: Codec[PlaylistTrack] = deriveConfiguredCodec

  implicit val publicUserCodec: Codec[PublicUser] = Codec.from(PublicUserCirce.decoder, deriveConfiguredEncoder)
  implicit val privateUserCodec
    : Codec[PrivateUser] = deriveConfiguredCodec // FIXME: I think images might be null, see PublicUser

  implicit val restrictionsCodec: Codec[Restrictions] = deriveConfiguredCodec
  implicit val simpleAlbumCodec: Codec[SimpleAlbum] = SimpleAlbumCirce.codec
  implicit val simplePlaylistCodec: Codec[SimplePlaylist] = deriveConfiguredCodec
  implicit val featuredPlaylistsCodec: Codec[FeaturedPlaylists] = deriveConfiguredCodec
  implicit val snapshotIdResponseCodec: Codec[SnapshotIdResponse] = deriveConfiguredCodec
  implicit val snapshotIdCodec: Codec[SnapshotId] = deriveUnwrappedCodec
  implicit val spotifyCategoryIdCodec: Codec[SpotifyCategoryId] = deriveUnwrappedCodec
  implicit val spotifyCategoryNameCodec: Codec[CategoryName] = deriveUnwrappedCodec
  implicit val spotifyErrorDecoder: Decoder[SpotifyError] = SpotifyErrorCirce.spotifyErrorDecoder
  implicit val spotifyIdCodec: Codec[SpotifyId] = deriveUnwrappedCodec
  implicit val spotifyArtistIdCodec: Codec[SpotifyArtistId] = deriveUnwrappedCodec
  implicit val spotifyAlbumIdCodec: Codec[SpotifyAlbumId] = deriveUnwrappedCodec
  implicit val spotifyUserIdCodec: Codec[SpotifyUserId] = deriveUnwrappedCodec
  implicit val spotifyPlaylistIdCodec: Codec[SpotifyPlaylistId] = deriveUnwrappedCodec
  implicit val spotifyPlaylistNameCodec: Codec[SpotifyPlaylistName] = deriveUnwrappedCodec
  implicit val playlistsResponseCodec: Codec[PlaylistsResponse] = deriveConfiguredCodec
  implicit val spotifyTrackIdCodec: Codec[SpotifyTrackId] = deriveUnwrappedCodec
  implicit val spotifyImageCodec: Codec[SpotifyImage] = deriveConfiguredCodec
  implicit val spotifyUriCodec: Codec[SpotifyUri] = deriveUnwrappedCodec
  implicit val spotifyUrlCodec: Codec[SpotifyUrl] = deriveUnwrappedCodec
  implicit val spotifyGenreCodec: Codec[SpotifyGenre] = deriveUnwrappedCodec
  implicit val spotifyGenresResponseCodec: Codec[SpotifyGenresResponse] = deriveConfiguredCodec
  implicit val subscriptionLevelCodec: Codec[SubscriptionLevel] = SubscriptionLevelCirce.codec
  implicit val barCodec: Codec[Bar] = TimeIntervalCirce.barCodec
  implicit val beatCodec: Codec[Beat] = TimeIntervalCirce.beatCodec
  implicit val tatumCodec: Codec[Tatum] = TimeIntervalCirce.tatumCodec

  implicit val categoryCodec: Codec[Category] = deriveConfiguredCodec
  implicit val categoriesResponseCodec: Codec[CategoriesResponse] = deriveConfiguredCodec

  implicit val simpleArtistCodec: Codec[SimpleArtist] = Codec.from(SimpleArtistCirce.decoder, deriveConfiguredEncoder)

  private val simpleTrackDecoder: Decoder[SimpleTrack] = {
    implicit val availableMarketsDecoder: Decoder[List[CountryCodeAlpha2]] = optionalCountryCodeList
    deriveConfiguredDecoder
  }
  implicit val simpleTrackCodec: Codec[SimpleTrack] = Codec.from(simpleTrackDecoder, deriveConfiguredEncoder)

  implicit val releaseDateDecoder: Decoder[ReleaseDate] = ReleaseDateCirce.decoder

  implicit val fullArtistCodec: Codec[FullArtist] = deriveConfiguredCodec
  implicit val artistsResponseCodec: Codec[ArtistsResponse] = deriveConfiguredCodec
  implicit val fullAlbumCodec: Codec[FullAlbum] = FullAlbumCirce.codec

  implicit val recommendationSeedCodec: Codec[RecommendationSeed] = deriveConfiguredCodec
  implicit val recommendationsCodec: Codec[Recommendations] = deriveConfiguredCodec

  implicit val fullAlbumsResponseCodec: Codec[FullAlbumsResponse] = deriveConfiguredCodec

  implicit val addTracksToPlaylistRequestEncoder: Encoder[AddTracksToPlaylistRequest] =
    dropNullValues(deriveConfiguredEncoder[AddTracksToPlaylistRequest])

  implicit val createPlaylistRequestEncoder: Encoder[CreatePlaylistRequest] =
    dropNullValues(deriveConfiguredEncoder[CreatePlaylistRequest])

  implicit val modifyPlaylistRequestEncoder: Encoder[ModifyPlaylistRequest] =
    dropNullValues(deriveConfiguredEncoder[ModifyPlaylistRequest])
}
