package it.turingtest.spotify.scala.client

import javax.inject.Inject

import it.turingtest.spotify.scala.client.logging.AccessLogging
import play.api.libs.ws.WSClient
import play.api.mvc.Controller

class SpotifyAPI @Inject()(base_api: BaseApi, profiles_endpoint: ProfilesApi, browse_endpoint: BrowseApi,
                           playlists_endpoint: PlaylistsApi, tracks_endpoint: TracksApi,
                           ws: WSClient) extends Controller with AccessLogging {

  final val api: BaseApi = base_api
  final val browse: BrowseApi = browse_endpoint
  final val profiles: ProfilesApi = profiles_endpoint
  final val playlists: PlaylistsApi = playlists_endpoint
  final val tracks: TracksApi = tracks_endpoint

}
