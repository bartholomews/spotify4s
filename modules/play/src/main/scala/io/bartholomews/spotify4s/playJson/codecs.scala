package io.bartholomews.spotify4s.playJson

import enumeratum.EnumFormats
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.{AccessToken, RefreshToken, ResponseHandler}
import io.bartholomews.fsclient.core.oauth.{AccessTokenSigner, NonRefreshableTokenSigner, Scope}
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
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.JsonConfiguration.Aux
import play.api.libs.json.JsonNaming.SnakeCase
import play.api.libs.json.{JsError, JsPath, JsString, JsSuccess, Json, JsonConfiguration, JsonNaming, Reads, Writes}
import sttp.client3.playJson.asJson
import sttp.model.Uri

object codecs extends SpotifyPlayJsonApi

//noinspection DuplicatedCode
// TODO: move into fsclient
trait FsClientPlayJsonApi {
  implicit def responseHandler[T](implicit decoder: Reads[T]): ResponseHandler[JsError, T] =
    asJson[T]

  implicit val accessTokenDecoder: Reads[AccessToken] = Json.valueReads
  implicit val refreshTokenDecoder: Reads[RefreshToken] = Json.valueReads

  implicit val scopeDecoder: Reads[Scope] = Reads
    .optionNoError[String]
    .map(_.fold(Scope(List.empty))(str => Scope(str.split(" ").toList)))

  implicit val accessTokenSignerDecoder: Reads[AccessTokenSigner] =
    (JsPath \ "generated_at")
      .read[Long]
      .orElse(Reads.pure(System.currentTimeMillis))
      .and((JsPath \ "access_token").read[AccessToken])
      .and((JsPath \ "token_type").read[String])
      .and((JsPath \ "expires_in").read[Long])
      .and((JsPath \ "refresh_token").readNullable[RefreshToken])
      .and((JsPath \ "scope").read[Scope].orElse(Reads.pure(Scope(List.empty))))(AccessTokenSigner.apply _)

  implicit val nonRefreshableTokenSignerDecoder: Reads[NonRefreshableTokenSigner] =
    (JsPath \ "generated_at")
      .read[Long]
      .orElse(Reads.pure(System.currentTimeMillis))
      .and((JsPath \ "access_token").read[AccessToken])
      .and((JsPath \ "token_type").read[String])
      .and((JsPath \ "expires_in").read[Long])
      .and((JsPath \ "scope").read[Scope].orElse(Reads.pure(Scope(List.empty))))(NonRefreshableTokenSigner.apply _)

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
