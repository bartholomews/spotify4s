package io.bartholomews.spotify4s.playJson

import enumeratum.EnumFormats
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.ResponseHandler
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities.TimeInterval.{Bar, Beat, Tatum}
import io.bartholomews.spotify4s.core.entities.{
  AlbumGroup,
  AlbumType,
  ApiError,
  AudioAnalysis,
  AudioFeatures,
  AudioFeaturesResponse,
  AudioSection,
  AudioSegment,
  AuthError,
  CollectionLink,
  Confidence,
  ExternalIds,
  ExternalResourceUrl,
  Followers,
  FullTrack,
  FullTracksResponse,
  LinkedTrack,
  Modality,
  NewReleases,
  Page,
  PitchClass,
  PrivateUser,
  PublicUser,
  Restrictions,
  SimpleAlbum,
  SimpleArtist,
  SimplePlaylist,
  SpotifyError,
  SpotifyId,
  SpotifyImage,
  SpotifyUri,
  SpotifyUserId,
  SubscriptionLevel,
  Tempo
}
import play.api.libs.json.JsonConfiguration.Aux
import play.api.libs.json.JsonNaming.SnakeCase
import play.api.libs.json.{JsError, JsString, JsSuccess, Json, JsonConfiguration, JsonNaming, Reads, Writes}
import sttp.client3.playJson.asJson
import sttp.model.Uri

object codecs extends SpotifyPlayJsonApi

// TODO: move into fsclient
trait FsClientPlayJsonApi {
  implicit def responseHandler[T](implicit decoder: Reads[T]): ResponseHandler[JsError, T] =
    asJson[T]

  implicit val uriEncoder: Writes[Uri] = (o: Uri) => JsString(o.toString)
  implicit val uriDecoder: Reads[Uri] = {
    case JsString(value) => Uri.parse(value).fold(JsError.apply, uri => JsSuccess(uri))
    case other => JsError(s"Expected a json string, got [$other]")
  }
}

trait SpotifyPlayJsonApi extends FsClientPlayJsonApi {
  implicit val config: Aux[Json.MacroOptions] = JsonConfiguration(SnakeCase)
  val withDiscriminator: Json.WithOptions[Json.MacroOptions] = {
    Json.configured(
      JsonConfiguration(
        discriminator = "type",
        typeNaming = JsonNaming { fullyQualifiedName =>
          val fr: String = fullyQualifiedName.split("\\.").last
          val tail = fr.tail.foldLeft("") { (acc, curr) =>
            if (curr.isUpper) s"${acc}_${curr.toLower}" else s"$acc$curr"
          }
          s"${fr.head.toLower}$tail"
        }
      )
    )
  }
  implicit def pageDecoder[T](implicit decoder: Reads[T]): Reads[Page[T]] = Json.reads[Page[T]]

  implicit val spotifyErrorDecoder: Reads[SpotifyError] = SpotifyErrorPlayJson.spotifyErrorReads
  implicit val spotifyIdDecoder: Reads[SpotifyId] = Json.valueReads[SpotifyId]
  implicit val spotifyImageDecoder: Reads[SpotifyImage] = Json.reads[SpotifyImage]
  implicit val spotifyUriDecoder: Reads[SpotifyUri] = Json.valueReads[SpotifyUri]
  implicit val spotifyUserIdDecoder: Reads[SpotifyUserId] = Json.valueReads[SpotifyUserId]
  implicit val confidenceDecoder: Reads[Confidence] = Json.valueReads[Confidence]
  implicit val barDecoder: Reads[Bar] = TimeIntervalPlayJson.barDecoder
  implicit val beatDecoder: Reads[Beat] = TimeIntervalPlayJson.beatDecoder
  implicit val tatumDecoder: Reads[Tatum] = TimeIntervalPlayJson.tatumDecoder
  implicit val tempoDecoder: Reads[Tempo] = TempoPlayJson.reads
  implicit val audioSectionDecoder: Reads[AudioSection] = AudioSectionPlayJson.reads
  implicit val audioSegmentDecoder: Reads[AudioSegment] = AudioSegmentPlayJson.reads
  implicit val modalityDecoder: Reads[Modality] = ModalityPlayJson.reads
  implicit val pitchClassDecoder: Reads[PitchClass] = Json.valueReads[PitchClass]
  implicit val audioFeaturesDecoder: Reads[AudioFeatures] = Json.reads[AudioFeatures]
  implicit val audioAnalysisDecoder: Reads[AudioAnalysis] = Json.reads[AudioAnalysis]
  implicit val albumGroupDecoder: Reads[AlbumGroup] = EnumFormats.reads(AlbumGroup)
  implicit val albumTypeDecoder: Reads[AlbumType] = EnumFormats.reads(AlbumType)
  implicit val audioFeaturesResponseDecoder: Reads[AudioFeaturesResponse] = Json.reads[AudioFeaturesResponse]
  implicit val apiErrorDecoder: Reads[ApiError] = SpotifyErrorPlayJson.apiErrorReads
  implicit val authErrorDecoder: Reads[AuthError] = SpotifyErrorPlayJson.authErrorReads
  implicit val countryCodeAlpha2Decoder: Reads[CountryCodeAlpha2] = CountryCodeAlpha2PlayJson.reads
  implicit val externalIdsDecoder: Reads[ExternalIds] = ExternalIdsPlayJson.reads
  implicit val externalResourceUrlDecoder: Reads[ExternalResourceUrl] = ExternalResourceUrlPlayJson.reads
  implicit val followersDecoder: Reads[Followers] = Json.reads[Followers]
  implicit val linkedTrackDecoder: Reads[LinkedTrack] = Json.reads[LinkedTrack]
  implicit val restrictionsDecoder: Reads[Restrictions] = Json.reads[Restrictions]
  implicit val simpleAlbumDecoder: Reads[SimpleAlbum] = SimpleAlbumPlayJson.reads
  implicit val simpleArtistDecoder: Reads[SimpleArtist] = SimpleArtistPlayJson.reads
  implicit val fullTrackDecoder: Reads[FullTrack] = FullTrackPlayJson.reads
  implicit val fullTracksResponseDecoder: Reads[FullTracksResponse] = Json.reads[FullTracksResponse]
  implicit val subscriptionLevelDecoder: Reads[SubscriptionLevel] = EnumFormats.reads(SubscriptionLevel)
  implicit val privateUserDecoder: Reads[PrivateUser] = Json.reads[PrivateUser]
  implicit val collectionLinkDecoder: Reads[CollectionLink] = Json.reads[CollectionLink]
  implicit val newReleasesDecoder: Reads[NewReleases] = Json.reads[NewReleases]
  implicit val publicUserDecoder: Reads[PublicUser] = PublicUserPlayJson.reads
  implicit val simplePlaylistDecoder: Reads[SimplePlaylist] = Json.reads[SimplePlaylist]
}
