package io.bartholomews.spotify4s.samples

object RefinedReadme extends App {
  import cats.data.NonEmptyList
  import io.bartholomews.spotify4s.core.api.SpotifyApi.SpotifyUris
  import io.bartholomews.spotify4s.core.entities.SpotifyUri

  // type SpotifyUris = Refined[NonEmptyList[SpotifyUri], MaxSize[100]]

  val ex1: Either[String, SpotifyUris] = SpotifyUri.fromList(List.empty)
  assert(ex1 == Left("Predicate failed: need to provide at least one uri."))

  val tooManyUris: NonEmptyList[SpotifyUri] = NonEmptyList.fromListUnsafe(
    (1 to 101).map(_ => SpotifyUri("just one extra uri...")).toList
  )

  val ex2: Either[String, SpotifyUris] = SpotifyUri.fromNel(tooManyUris)
  assert(ex2 == Left("Predicate failed: a maximum of 100 uris can be set in one request."))
}
