package io.bartholomews.spotify4s.core

import cats.Monad
import eu.timepit.refined.api.Refined
import io.bartholomews.fsclient.core.config.UserAgent
import io.bartholomews.fsclient.core.http.SttpResponses.{ResponseHandler, SttpResponse}
import io.bartholomews.fsclient.core.oauth.v2.ClientPassword
import io.bartholomews.fsclient.core.oauth.{ClientPasswordAuthentication, NonRefreshableTokenSigner, TokenSignerV2}
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.api.AlbumsApi.AlbumIds
import io.bartholomews.spotify4s.core.api.FollowApi.UserIdsFollowingPlaylist
import io.bartholomews.spotify4s.core.api.SpotifyApi.Offset
import io.bartholomews.spotify4s.core.api.TracksApi.{
  AudioFeaturesTrackIds,
  RecommendationSeedRequest,
  RecommendationsLimit,
  TrackIds
}
import io.bartholomews.spotify4s.core.api.{AlbumsApi, BrowseApi}
import io.bartholomews.spotify4s.core.entities.SpotifyId.{
  SpotifyAlbumId,
  SpotifyPlaylistId,
  SpotifyTrackId,
  SpotifyUserId
}
import io.bartholomews.spotify4s.core.entities._
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures
import sttp.client3.{Response, ResponseException, SttpBackend}

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicReference

/**
  * This client has a subset of available endpoints available,
  * as it is not suitable for user-related endpoints (it would return 401);
  * it manages (and refresh) a single Client Credentials token.
  */
class SpotifySimpleClient[F[_]: Monad] private (client: SpotifyAuthClient[F]) {
  def this(userAgent: UserAgent, clientPassword: ClientPassword, backend: SttpBackend[F, Any]) =
    this(new SpotifyAuthClient(userAgent, clientPassword, backend))

  import eu.timepit.refined.auto.autoRefineV
  type S = ClientPasswordAuthentication

  private val signerRef: AtomicReference[Option[TokenSignerV2]] = new AtomicReference(None)

  import cats.implicits._

  private def acquireToken[DE, A](f: TokenSignerV2 => F[SttpResponse[DE, A]])(
    implicit tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner]
  ): F[SttpResponse[DE, A]] = {
    client.auth.clientCredentials.flatMap(
      (response: SttpResponse[DE, NonRefreshableTokenSigner]) =>
        response.body.fold(
          err => response.copy(body = err.asLeft[A]).pure[F],
          newToken => {
            signerRef.set(Some(newToken))
            f(newToken)
          }
        )
    )
  }

  private def withToken[DE, A](f: TokenSignerV2 => F[SttpResponse[DE, A]])(
    implicit tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner]
  ): F[SttpResponse[DE, A]] =
    signerRef
      .get()
      .fold(acquireToken(f))(
        token =>
          if (token.isExpired()) acquireToken(f)
          else f(token)
      )

  object albums {
    def getAlbum[DE](id: SpotifyAlbumId, country: Option[CountryCodeAlpha2])(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, FullAlbum]
    ): F[SttpResponse[DE, FullAlbum]] = withToken { client.albums.getAlbum(id, country) }

    def getAlbums[DE](ids: AlbumIds, market: Option[CountryCodeAlpha2])(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, FullAlbumsResponse]
    ): F[SttpResponse[DE, List[FullAlbum]]] = withToken { client.albums.getAlbums(ids, market) }

    def getAlbumTracks[DE](
      id: SpotifyAlbumId,
      market: Option[CountryCodeAlpha2],
      limit: AlbumsApi.Limit = 20,
      offset: Offset = 0
    )(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, Page[SimpleTrack]]
    ): F[SttpResponse[DE, Page[SimpleTrack]]] =
      withToken { client.albums.getAlbumTracks(id, market, limit, offset) }

    def getNewReleases[DE](country: Option[CountryCodeAlpha2], limit: BrowseApi.Limit = 20, offset: Offset = 0)(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, NewReleases]
    ): F[SttpResponse[DE, NewReleases]] = withToken { client.albums.getNewReleases(country, limit, offset) }
  }

  object browse {
    def getAllFeaturedPlaylists[DE](
      country: Option[CountryCodeAlpha2],
      locale: Option[Locale],
      timestamp: Option[LocalDateTime],
      limit: BrowseApi.Limit = 20,
      offset: Offset = 0
    )(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, FeaturedPlaylists]
    ): F[SttpResponse[DE, FeaturedPlaylists]] = withToken {
      client.browse.getAllFeaturedPlaylists(country, locale, timestamp, limit, offset)
    }

    def getAllCategories[DE](
      country: Option[CountryCodeAlpha2],
      locale: Option[Locale],
      limit: BrowseApi.Limit = 20,
      offset: Offset = 0
    )(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, CategoriesResponse]
    ): F[SttpResponse[DE, Page[Category]]] = withToken {
      client.browse.getAllCategories(country, locale, limit, offset)
    }

    def getCategory[DE](
      categoryId: SpotifyCategoryId,
      country: Option[CountryCodeAlpha2],
      locale: Option[Locale]
    )(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, Category]
    ): F[SttpResponse[DE, Category]] = withToken {
      client.browse.getCategory(categoryId, country, locale)
    }

    def getCategoryPlaylists[DE](
      categoryId: SpotifyCategoryId,
      country: Option[CountryCodeAlpha2],
      limit: BrowseApi.Limit = 20,
      offset: Offset = 0
    )(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, PlaylistsResponse]
    ): F[SttpResponse[DE, Page[SimplePlaylist]]] = withToken {
      client.browse.getCategoryPlaylists(categoryId, country, limit, offset)
    }

    def getRecommendationGenres[DE]()(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, SpotifyGenresResponse]
    ): F[SttpResponse[DE, List[SpotifyGenre]]] = withToken { signer =>
      client.browse.getRecommendationGenres(signer)
    }
  }

  object follow {
    def usersFollowingPlaylist[DE](playlistId: SpotifyPlaylistId, userIds: UserIdsFollowingPlaylist)(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, List[Boolean]]
    ): F[SttpResponse[DE, Map[SpotifyUserId, Boolean]]] =
      withToken { client.follow.usersFollowingPlaylist(playlistId, userIds) }
  }

  object tracks {
    def getTrack[DE](id: SpotifyTrackId, market: Option[Market])(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, FullTrack]
    ): F[SttpResponse[DE, FullTrack]] = withToken { client.tracks.getTrack(id, market) }

    def getTracks[DE](ids: TrackIds, market: Option[Market])(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, FullTracksResponse]
    ): F[SttpResponse[DE, List[FullTrack]]] = withToken { client.tracks.getTracks(ids, market) }

    def getAudioFeatures[DE](ids: AudioFeaturesTrackIds)(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, AudioFeaturesResponse]
    ): F[SttpResponse[DE, List[AudioFeatures]]] =
      withToken { client.tracks.getAudioFeatures(ids) }

    def getAudioFeatures[DE](id: SpotifyTrackId)(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, AudioFeatures]
    ): F[SttpResponse[DE, AudioFeatures]] =
      withToken { client.tracks.getAudioFeatures(id) }

    def getAudioAnalysis[DE](
      id: SpotifyTrackId
    )(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, AudioAnalysis]
    ): F[SttpResponse[DE, AudioAnalysis]] =
      withToken { client.tracks.getAudioAnalysis(id) }

    def getRecommendations[DE](
      limit: RecommendationsLimit = 20,
      market: Option[Market],
      recommendationSeedRequest: RecommendationSeedRequest = Refined.unsafeApply(List.empty[RecommendationSeedQuery]),
      audioFeaturesQuery: AudioFeaturesQuery = AudioFeaturesQuery.empty
    )(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, Recommendations]
    ): F[SttpResponse[DE, Recommendations]] = withToken {
      client.tracks.getRecommendations(limit, market, recommendationSeedRequest, audioFeaturesQuery)
    }
  }

  object users {
    def getUserProfile[DE](userId: SpotifyUserId)(
      implicit
      tokenHandler: ResponseHandler[DE, NonRefreshableTokenSigner],
      responseHandler: ResponseHandler[DE, PublicUser]
    ): F[Response[Either[ResponseException[String, DE], PublicUser]]] =
      withToken { client.users.getUserProfile(userId) }
  }
}

object SpotifySimpleClient {
  import pureconfig.generic.auto._

  private val userAgentConfig = ConfigSource.default.at(namespace = "user-agent")
  private val spotifyConfig = ConfigSource.default.at("spotify")

  def fromConfig[F[_]: Monad](sttpBackend: SttpBackend[F, Any]): Either[ConfigReaderFailures, SpotifySimpleClient[F]] =
    for {
      userAgent <- userAgentConfig.load[UserAgent]
      clientPassword <- spotifyConfig.load[ClientPassword]
    } yield new SpotifySimpleClient[F](userAgent, clientPassword, sttpBackend)

  def unsafeFromConfig[F[_]: Monad](sttpBackend: SttpBackend[F, Any]): SpotifySimpleClient[F] = {
    val userAgent = userAgentConfig.loadOrThrow[UserAgent]
    val clientPassword = spotifyConfig.loadOrThrow[ClientPassword]
    new SpotifySimpleClient[F](userAgent, clientPassword, sttpBackend)
  }
}
