package io.bartholomews.spotify4s.core.entities

import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/#object-categoryobject
final case class Category(
  href: Uri,
  icons: List[SpotifyImage],
  id: SpotifyCategoryId,
  name: CategoryName
)

final case class CategoryName(value: String) extends AnyVal
final case class CategoriesResponse(categories: Page[Category])
