package it.turingtest.spotify.scala.client.entities

import play.api.data.validation.ValidationError
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

import scala.util.Try

case class Recommendation(seeds: Seq[RecommendationSeed],
                          tracks: Seq[SimpleTrack])

object Recommendation {
  implicit val recommendationReads: Reads[Recommendation] = (
    (JsPath \ "seeds").read[Seq[RecommendationSeed]] and
      (JsPath \ "tracks").read[Seq[SimpleTrack]]
  )(Recommendation.apply _)
}

case class RecommendationSeed
(
afterFilteringSize: Int,
afterRelinkingSize: Int,
href: String,
id: String,
initialPoolSize: Int,
seedType: SeedType
)

object RecommendationSeed {
  implicit val recommendationSeedReads: Reads[RecommendationSeed] = (
    (JsPath \ "afterFilteringSize").read[Int] and
      (JsPath \ "afterRelinkingSize").read[Int] and
      (JsPath \ "href").read[String] and
      (JsPath \ "id").read[String] and
      (JsPath \ "initialPoolSize").read[Int] and
      // @see https://stackoverflow.com/a/29948007
      (JsPath \ "type").read[String].collect(ValidationError(""))(
        Function.unlift(s => Try(SeedType.valueOf(s)).toOption)
      )
    )(RecommendationSeed.apply _)
}

sealed trait SeedType

object SeedType {
  def valueOf(value: String): SeedType = value.toLowerCase match {
    case "artist" => ArtistSeed
    case "track" => TrackSeed
    case "genre" => GenreSeed
    case other => throw new IllegalArgumentException(s"$other: invalid seed type.")
  }
}

case object ArtistSeed extends SeedType
case object TrackSeed extends SeedType
case object GenreSeed extends SeedType