package io.bartholomews.spotify4s.client

import cats.effect.{ContextShift, IO, Resource}
import io.bartholomews.scalatestudo.data.TestudoClientData
import io.bartholomews.spotify4s.SpotifyClient
import io.bartholomews.spotify4s.entities.{SpotifyId, SpotifyUserId}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext

object ClientData extends TestudoClientData {
  // https://http4s.org/v0.20/client/
  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global
  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(ec)
  implicit val resource: Resource[IO, Client[IO]] = BlazeClientBuilder[IO](ec).resource

  val sampleClient: SpotifyClient[IO] = new SpotifyClient(OAuthV2.sampleClient)
  val sampleSpotifyId: SpotifyId = SpotifyId("SAMPLE_SPOTIFY_ID")
  val sampleSpotifyUserId: SpotifyUserId = SpotifyUserId("SAMPLE_SPOTIFY_USER_ID")
}
