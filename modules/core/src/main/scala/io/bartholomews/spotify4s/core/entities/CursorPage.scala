package io.bartholomews.spotify4s.core.entities

import sttp.model.Uri

/**
  * https://developer.spotify.com/documentation/web-api/reference/#object-cursorpagingobject
  * @param href A link to the Web API endpoint returning the full result of the request.
  * @param items The requested data.
  * @param limit The maximum number of items in the response (as set in the query or by default).
  * @param next URL to the next page of items.
  * @param total The total number of items available to return.
  * @param cursors The cursors used to find the next set of items.
  * @tparam Id The type of resource items id
  * @tparam A The type of resource items
  */
final case class CursorPage[Id, A](
  href: Uri,
  items: List[A],
  limit: Option[Int],
  next: Option[String],
  total: Int,
  cursors: Cursor[Id]
)

/**
  * https://developer.spotify.com/documentation/web-api/reference/#object-cursorobject
  * @param after The cursor to use as key to find the next page of items.
  * @tparam Id The type of resource items id
  */
final case class Cursor[Id](after: Id)
