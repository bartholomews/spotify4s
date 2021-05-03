package io.bartholomews.spotify4s.playJson

import enumeratum.EnumFormats
import io.bartholomews.fsclient.play.FsClientPlayApi
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities.TimeInterval.{Bar, Beat, Tatum}
import io.bartholomews.spotify4s.core.entities.requests.{
  AddTracksToPlaylistRequest,
  CreatePlaylistRequest,
  ModifyPlaylistRequest
}
import io.bartholomews.spotify4s.core.entities._
import play.api.libs.json.JsonConfiguration.Aux
import play.api.libs.json.JsonNaming.SnakeCase
import play.api.libs.json._

object codecs extends SpotifyPlayJsonApi

trait SpotifyPlayJsonApi extends FsClientPlayApi {
  implicit val config: Aux[Json.MacroOptions] = JsonConfiguration(SnakeCase)
  def readNullableList[A](implicit rds: Reads[A]): Reads[List[A]] =
    (json: JsValue) =>
      json
        .validateOpt[List[A]](Reads.list(rds))
        .map(_.getOrElse(Nil))

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
  implicit def pageEncoder[T](implicit encoder: Writes[T]): Writes[Page[T]] = Json.writes[Page[T]]
  implicit def pageCodec[T](implicit codec: Format[T]): Format[Page[T]] = Json.format[Page[T]]

  implicit val modifyPlaylistRequestEncoder: Writes[ModifyPlaylistRequest] = Json.writes
  implicit val createPlaylistRequestEncoder: Writes[CreatePlaylistRequest] = Json.writes
  implicit val addTracksToPlaylistRequestEncoder: Writes[AddTracksToPlaylistRequest] = Json.writes

  implicit val copyrightCodec: Format[Copyright] = Json.format[Copyright]
  implicit val snapshotIdCodec: Format[SnapshotId] = Json.valueFormat
  implicit val snapshotIdResponseCodec: Format[SnapshotIdResponse] = Json.format
  implicit val spotifyErrorCodec: Format[SpotifyError] = SpotifyErrorPlayJson.spotifyErrorFormat
  implicit val spotifyIdCodec: Format[SpotifyId] = Json.valueFormat[SpotifyId]
  implicit val spotifyImageCodec: Format[SpotifyImage] = Json.format[SpotifyImage]
  implicit val spotifyUriCodec: Format[SpotifyUri] = Json.valueFormat[SpotifyUri]
  implicit val spotifyUserIdCodec: Format[SpotifyUserId] = Json.valueFormat[SpotifyUserId]
  implicit val confidenceCodec: Format[Confidence] = Json.valueFormat[Confidence]
  implicit val barCodec: Format[Bar] = TimeIntervalPlayJson.barFormat
  implicit val beatCodec: Format[Beat] = TimeIntervalPlayJson.beatFormat
  implicit val tatumCodec: Format[Tatum] = TimeIntervalPlayJson.tatumFormat
  implicit val tempoCodec: Format[Tempo] = TempoPlayJson.format
  implicit val audioSectionCodec: Format[AudioSection] = AudioSectionPlayJson.format
  implicit val audioSegmentCodec: Format[AudioSegment] = AudioSegmentPlayJson.format
  implicit val modalityCodec: Format[Modality] = ModalityPlayJson.format
  implicit val pitchClassCodec: Format[PitchClass] = Json.valueFormat[PitchClass]
  implicit val audioFeaturesCodec: Format[AudioFeatures] = Json.format[AudioFeatures]
  implicit val audioAnalysisCodec: Format[AudioAnalysis] = Json.format[AudioAnalysis]
  implicit val albumGroupCodec: Format[AlbumGroup] = EnumFormats.formats(AlbumGroup)
  implicit val albumTypeCodec: Format[AlbumType] = EnumFormats.formats(AlbumType)
  implicit val audioFeaturesResponseCodec: Format[AudioFeaturesResponse] = Json.format[AudioFeaturesResponse]
  implicit val apiErrorCodec: Format[ApiError] = SpotifyErrorPlayJson.apiErrorFormat
  implicit val authErrorCodec: Format[AuthError] = SpotifyErrorPlayJson.authErrorFormat
  implicit val countryCodeAlpha2Codec: Format[CountryCodeAlpha2] = CountryCodeAlpha2PlayJson.format
  implicit val externalIdsCodec: Format[ExternalIds] = ExternalIdsPlayJson.format
  implicit val externalResourceUrlCodec: Format[ExternalResourceUrl] = ExternalResourceUrlPlayJson.format
  implicit val followersCodec: Format[Followers] = Json.format[Followers]
  implicit val linkedTrackCodec: Format[LinkedTrack] = Json.format[LinkedTrack]
  implicit val restrictionsCodec: Format[Restrictions] = Json.format[Restrictions]
  implicit val simpleAlbumCodec: Format[SimpleAlbum] = SimpleAlbumPlayJson.format
  implicit val simpleArtistCodec: Format[SimpleArtist] = SimpleArtistPlayJson.format
  implicit val publicUserCodec: Format[PublicUser] = PublicUserPlayJson.format
  implicit val fullTrackCodec: Format[FullTrack] = FullTrackPlayJson.format
  implicit val playlistTrackCodec: Format[PlaylistTrack] = Json.format
  implicit val fullPlaylistCodec: Format[FullPlaylist] = Json.format[FullPlaylist]
  implicit val fullTracksResponseCodec: Format[FullTracksResponse] = Json.format[FullTracksResponse]
  implicit val subscriptionLevelCodec: Format[SubscriptionLevel] = EnumFormats.formats(SubscriptionLevel)
  implicit val privateUserCodec: Format[PrivateUser] = Json.format[PrivateUser]
  implicit val collectionLinkCodec: Format[CollectionLink] = Json.format[CollectionLink]
  implicit val newReleasesCodec: Format[NewReleases] = Json.format[NewReleases]
  implicit val simplePlaylistCodec: Format[SimplePlaylist] = Json.format[SimplePlaylist]
  // TODO: Format not Reads
  implicit val simpleTrackDecoder: Reads[SimpleTrack] = SimpleTrackPlayJson.reads
  implicit val fullArtistDecoder: Reads[FullArtist] = Json.reads[FullArtist]
  implicit val fullAlbumDecoder: Reads[FullAlbum] = {
    implicit val opt: Reads[List[CountryCodeAlpha2]] = readNullableList[CountryCodeAlpha2]
    FullAlbumPlayJson.reads
  }
  implicit val fullAlbumsResponseDecoder: Reads[FullAlbumsResponse] = Json.reads[FullAlbumsResponse]
}
