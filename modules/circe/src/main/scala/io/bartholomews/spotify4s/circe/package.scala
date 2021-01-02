package io.bartholomews.spotify4s

import enumeratum.Circe
import io.bartholomews.fsclient.circe.FsClientCirceApi
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities.TimeInterval.{Bar, Beat, Tatum}
import io.bartholomews.spotify4s.core.entities.requests.{
  AddTracksToPlaylistRequest,
  CreatePlaylistRequest,
  ModifyPlaylistRequest
}
import io.bartholomews.spotify4s.core.entities.{
  AlbumGroup,
  AlbumType,
  AudioAnalysis,
  AudioFeatures,
  AudioFeaturesResponse,
  AudioKey,
  AudioMode,
  AudioSection,
  AudioSegment,
  CollectionLink,
  Confidence,
  ExternalIds,
  ExternalResourceUrl,
  Followers,
  FullEpisode,
  FullPlaylist,
  FullTrack,
  FullTracksResponse,
  LinkedTrack,
  Loudness,
  Modality,
  NewReleases,
  Page,
  PitchClass,
  PlaylistTrack,
  PrivateUser,
  PublicUser,
  ReleaseDate,
  Restrictions,
  SimpleAlbum,
  SimpleArtist,
  SimplePlaylist,
  SimplePlaylistItem,
  SnapshotId,
  SpotifyCategoryId,
  SpotifyError,
  SpotifyId,
  SpotifyImage,
  SpotifyScope,
  SpotifyUri,
  SpotifyUrl,
  SpotifyUserId,
  SubscriptionLevel,
  Tempo,
  TimeSignature
}
import io.circe.Decoder.Result
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{
  deriveConfiguredCodec,
  deriveConfiguredDecoder,
  deriveConfiguredEncoder,
  deriveUnwrappedCodec
}
import io.circe.{Codec, Decoder, DecodingFailure, Encoder, HCursor}

package object circe extends FsClientCirceApi {
  implicit val defaultConfig: Configuration = Configuration.default.withSnakeCaseMemberNames

  implicit def pageDecoder[A](implicit decoder: Decoder[A]): Decoder[Page[A]] = CircePage.decoder
  implicit def pageEncoder[A](implicit encoder: Encoder[A]): Encoder[Page[A]] = CircePage.encoder

  implicit val spotifyScopeCodec: Codec[SpotifyScope] =
    Codec.from(Circe.decoder(SpotifyScope), Circe.encoder(SpotifyScope))

  implicit val snapshotIdDecoder: Decoder[SnapshotId] = (c: HCursor) =>
    c.downField("snapshot_id").as[String].map(SnapshotId.apply)

  implicit val albumGroupCodec: Codec[AlbumGroup] = Codec.from(Circe.decoder(AlbumGroup), Circe.encoder(AlbumGroup))
  implicit val albumTypeCodec: Codec[AlbumType] = Codec.from(Circe.decoder(AlbumType), Circe.encoder(AlbumType))
  implicit val audioAnalysisDecoder: Decoder[AudioAnalysis] = deriveConfiguredDecoder
  // Decoder.decodeList[AudioFeatures].at("audio_features")
  implicit val tatumCodec: Codec[Tatum] = CirceTimeInterval.tatumCodec
  implicit val audioFeaturesDecoder: Decoder[AudioFeatures] = deriveConfiguredDecoder
  implicit val audioFeaturesResponseDecoder: Decoder[AudioFeaturesResponse] = deriveConfiguredDecoder
  implicit val audioSectionCodec: Codec[AudioSection] = CirceAudioSection.codec
  implicit val audioSegmentCodec: Codec[AudioSegment] = CirceAudioSegment.codec
  implicit val barCodec: Codec[Bar] = CirceTimeInterval.barCodec
  implicit val beatCodec: Codec[Beat] = CirceTimeInterval.beatCodec
  implicit val confidenceCodec: Codec[Confidence] = deriveUnwrappedCodec
  implicit val collectionLinkDecoder: Decoder[CollectionLink] = deriveConfiguredDecoder
  implicit val externalIdsCodec: Codec[ExternalIds] = Codec.from(CirceExternalIds.decoder, CirceExternalIds.encoder)
  implicit val externalResourceUrlCodec: Codec[ExternalResourceUrl] = CirceExternalResourceUrl.codec
  implicit val followersDecoder: Decoder[Followers] = deriveConfiguredDecoder
  implicit val fullEpisodeDecoder: Decoder[FullEpisode] = deriveConfiguredDecoder
  implicit val fullPlaylistDecoder: Decoder[FullPlaylist] = deriveConfiguredDecoder
  implicit val fullTrackDecoder: Decoder[FullTrack] = CirceFullTrack.decoder
  implicit val fullTracksResponseDecoder: Decoder[FullTracksResponse] = deriveConfiguredDecoder
  implicit val linkedTrackDecoder: Decoder[LinkedTrack] = deriveConfiguredDecoder
  implicit val modalityCodec: Codec[Modality] = CirceModality.codec
  implicit val newReleasesDecoder: Decoder[NewReleases] = deriveConfiguredDecoder
  implicit val pitchClassCodec: Codec[PitchClass] = deriveUnwrappedCodec
  implicit val playlistTrackDecoder: Decoder[PlaylistTrack] = deriveConfiguredDecoder
  implicit val privateUserDecoder: Decoder[PrivateUser] = deriveConfiguredDecoder
  implicit val publicUserDecoder: Decoder[PublicUser] = CircePublicUser.decoder
  implicit val releaseDateCodec: Codec[ReleaseDate] = CirceReleaseDate.codec
  implicit val restrictionsDecoder: Codec[Restrictions] = deriveConfiguredCodec
  implicit val simpleAlbumCodec: Decoder[SimpleAlbum] = CirceSimpleAlbum.decoder
  implicit val simpleArtistDecoder: Codec[SimpleArtist] = CirceSimpleArtist.codec
  implicit val simplePlaylistDecoder: Decoder[SimplePlaylist] = deriveConfiguredDecoder
  implicit val spotifyCategoryIdCodec: Codec[SpotifyCategoryId] = deriveUnwrappedCodec
  implicit val spotifyErrorDecoder: Decoder[SpotifyError] = CirceSpotifyError.spotifyErrorDecoder
  implicit val spotifyIdCodec: Codec[SpotifyId] = deriveUnwrappedCodec
  implicit val spotifyImageDecoder: Codec[SpotifyImage] = deriveConfiguredCodec
  implicit val spotifyUriCodec: Codec[SpotifyUri] = deriveUnwrappedCodec
  implicit val spotifyUrlCodec: Codec[SpotifyUrl] = deriveUnwrappedCodec
  implicit val spotifyUserIdCodec: Codec[SpotifyUserId] = deriveUnwrappedCodec
  implicit val subscriptionLevelCodec: Codec[SubscriptionLevel] = CirceSubscriptionLevel.codec

  implicit val addTracksToPlaylistRequestEncoder: Encoder[AddTracksToPlaylistRequest] =
    dropNullValues(deriveConfiguredEncoder[AddTracksToPlaylistRequest])

  implicit val createPlaylistRequestEncoder: Encoder[CreatePlaylistRequest] =
    dropNullValues(deriveConfiguredEncoder[CreatePlaylistRequest])

  implicit val modifyPlaylistRequestEncoder: Encoder[ModifyPlaylistRequest] =
    dropNullValues(deriveConfiguredEncoder[ModifyPlaylistRequest])

  // Custom decoders merging two fields in AudioSegment/AudioSection:
  implicit val audioKeyEncoder: Encoder[AudioKey] = deriveConfiguredEncoder
  implicit val audioModeEncoder: Encoder[AudioMode] = deriveConfiguredEncoder
  implicit val loudnessEncoder: Encoder[Loudness] = deriveConfiguredEncoder
  implicit val tempoEncoder: Encoder[Tempo] = deriveConfiguredEncoder
  implicit val timeSignatureEncoder: Encoder[TimeSignature] = deriveConfiguredEncoder

  implicit val simplePlaylistItemDecoder: Decoder[SimplePlaylistItem] = (c: HCursor) =>
    // TODO: decode with "type" discriminator ("track" / "episode")
    //  also double check optional (aka empty object) externalResourceUrl issue
    Left(DecodingFailure("TODO: decode with \"type\" discriminator (\"track\" / \"episode\")", c.history))
}
