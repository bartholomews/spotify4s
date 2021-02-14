package io.bartholomews.spotify4s.playJson

import enumeratum.EnumFormats
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities.{
  AlbumGroup,
  AlbumType,
  ApiError,
  AuthError,
  CollectionLink,
  ExternalResourceUrl,
  Followers,
  Page,
  PrivateUser,
  ReleaseDate,
  Restrictions,
  SpotifyError,
  SpotifyId,
  SpotifyImage,
  SpotifyUri,
  SpotifyUserId,
  SubscriptionLevel
}
import play.api.libs.json.{Json, Writes}

object encoders {
  import io.bartholomews.spotify4s.playJson.codecs.sttpUriEncoder
  implicit val followersEncoder: Writes[Followers] = Json.writes[Followers]
  implicit val spotifyIdEncoder: Writes[SpotifyId] = Json.valueWrites[SpotifyId]
  implicit val spotifyUriEncoder: Writes[SpotifyUri] = Json.valueWrites[SpotifyUri]
  implicit val spotifyUserIdEncoder: Writes[SpotifyUserId] = Json.valueWrites[SpotifyUserId]
  implicit val spotifyImageEncoder: Writes[SpotifyImage] = Json.writes[SpotifyImage]
  implicit val collectionLinkEncoder: Writes[CollectionLink] = Json.writes[CollectionLink]
  implicit val externalResourceUrlEncoder: Writes[ExternalResourceUrl] = ExternalResourceUrlPlayJson.writes
  implicit val countryCodeAlpha2Encoder: Writes[CountryCodeAlpha2] = CountryCodeAlpha2PlayJson.writes
  implicit val authErrorEncoder: Writes[AuthError] = SpotifyErrorPlayJson.authErrorWrites
  implicit val apiErrorEncoder: Writes[ApiError] = SpotifyErrorPlayJson.apiErrorWrites
  implicit val spotifyErrorEncoder: Writes[SpotifyError] = SpotifyErrorPlayJson.spotifyErrorWrites
  //  implicit val publicUserEncoder: Writes[PublicUser] = PublicUserPlayJson.writes
  implicit val albumGroupEncoder: Writes[AlbumGroup] = EnumFormats.writes(AlbumGroup)
  implicit val albumTypeEncoder: Writes[AlbumType] = EnumFormats.writes(AlbumType)
  implicit val subscriptionLevelEncoder: Writes[SubscriptionLevel] = EnumFormats.writes(SubscriptionLevel)
  implicit val restrictionsEncoder: Writes[Restrictions] = Json.writes[Restrictions]
  //  implicit val simpleArtistEncoder: Writes[SimpleArtist] = SimpleArtistPlayJson.writes
  implicit val releaseDateWrites: Writes[ReleaseDate] = ReleaseDatePlayJson.writes
  //  implicit val simpleAlbumEncoder: Writes[SimpleAlbum] = SimpleAlbumPlayJson.writes
  //  implicit val newReleasesEncoder: Writes[NewReleases] = Json.writes[NewReleases]
  implicit val privateUserEncoder: Writes[PrivateUser] = Json.writes[PrivateUser]
  //  implicit val simplePlaylistEncoder: Writes[SimplePlaylist] = Json.writes[SimplePlaylist]
  implicit def pageEncoder[T](implicit codec: Writes[T]): Writes[Page[T]] = Json.writes[Page[T]]
}
