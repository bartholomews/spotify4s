package io.bartholomews.spotify4s.circe

import io.bartholomews.fsclient.circe.FsClientCirceApi
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities.TimeInterval.{Bar, Beat, Tatum}
import io.bartholomews.spotify4s.core.entities.{AlbumGroup, AlbumType, AudioAnalysis, AudioFeatures, AudioFeaturesResponse, AudioSection, AudioSegment, CollectionLink, Confidence, ExternalResourceUrl, Followers, FullTrack, FullTracksResponse, LinkedTrack, Modality, NewReleases, Page, PitchClass, PrivateUser, PublicUser, Restrictions, SimpleAlbum, SimplePlaylist, SpotifyCategoryId, SpotifyError, SpotifyId, SpotifyImage, SpotifyUri, SpotifyUrl, SpotifyUserId, SubscriptionLevel}
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredCodec, deriveConfiguredDecoder, deriveUnwrappedCodec}
import io.circe.{Codec, Decoder}

object codecs extends SpotifyCirceApi with SpotifyCirceApiEncoders

trait SpotifyCirceApi extends FsClientCirceApi {
  import io.bartholomews.fsclient.circe.sttpUriCodec
  implicit val defaultConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val albumGroupDecoder: Decoder[AlbumGroup] = AlbumGroupCirce.decoder
  implicit val albumTypeDecoder: Decoder[AlbumType] = AlbumTypeCirce.decoder
  implicit val audioAnalysisDecoder: Decoder[AudioAnalysis] = deriveConfiguredDecoder
  implicit val audioFeaturesDecoder: Decoder[AudioFeatures] = deriveConfiguredDecoder
  implicit val audioFeaturesResponseDecoder: Decoder[AudioFeaturesResponse] = deriveConfiguredDecoder
  implicit val audioSectionDecoder: Decoder[AudioSection] = AudioSectionCirce.decoder
  implicit val audioSegmentDecoder: Decoder[AudioSegment] = AudioSegmentCirce.decoder
  implicit val collectionLinkCodec: Codec[CollectionLink] = deriveConfiguredCodec
  implicit val confidenceCodec: Codec[Confidence] = deriveUnwrappedCodec
  implicit val countryCodeAlpha2Codec: Codec[CountryCodeAlpha2] = CountryCodeAlpha2Circe.codec
  implicit val externalResourceUrlCodec: Codec[ExternalResourceUrl] = ExternalResourceUrlCirce.codec
  implicit val followersCodec: Codec[Followers] = deriveConfiguredCodec
  implicit val fullTrackDecoder: Decoder[FullTrack] = FullTrackCirce.decoder
  implicit val fullTracksResponseDecoder: Decoder[FullTracksResponse] = deriveConfiguredDecoder
  implicit val linkedTrackCodec: Codec[LinkedTrack] = deriveConfiguredCodec
  implicit val modalityCodec: Codec[Modality] = ModalityCirce.codec
  implicit val newReleasesDecoder: Decoder[NewReleases] = deriveConfiguredDecoder
  implicit def pageDecoder[A](implicit decoder: Decoder[A]): Decoder[Page[A]] = PageCirce.decoder
  implicit val pitchClassCodec: Codec[PitchClass] = deriveUnwrappedCodec
  implicit val publicUserDecoder: Decoder[PublicUser] = PublicUserCirce.decoder
  implicit val privateUserDecoder: Decoder[PrivateUser] = deriveConfiguredDecoder // FIXME: I think images might be null, see PublicUser
  implicit val restrictionsCodec: Codec[Restrictions] = deriveConfiguredCodec
  implicit val simpleAlbumDecoder: Decoder[SimpleAlbum] = SimpleAlbumCirce.decoder
  implicit val simplePlaylistDecoder: Decoder[SimplePlaylist] = deriveConfiguredDecoder
  implicit val spotifyCategoryIdCodec: Codec[SpotifyCategoryId] = deriveUnwrappedCodec
  implicit val spotifyErrorDecoder: Decoder[SpotifyError] = SpotifyErrorCirce.spotifyErrorDecoder
  implicit val spotifyIdCodec: Codec[SpotifyId] = deriveUnwrappedCodec
  implicit val spotifyImageCodec: Codec[SpotifyImage] = deriveConfiguredCodec
  implicit val spotifyUriCodec: Codec[SpotifyUri] = deriveUnwrappedCodec
  implicit val spotifyUrlCodec: Codec[SpotifyUrl] = deriveUnwrappedCodec
  implicit val spotifyUserIdCodec: Codec[SpotifyUserId] = deriveUnwrappedCodec
  implicit val subscriptionLevelCodec: Codec[SubscriptionLevel] = SubscriptionLevelCirce.codec
  implicit val barCodec: Codec[Bar] = TimeIntervalCirce.barCodec
  implicit val beatCodec: Codec[Beat] = TimeIntervalCirce.beatCodec
  implicit val tatumCodec: Codec[Tatum] = TimeIntervalCirce.tatumCodec
}
