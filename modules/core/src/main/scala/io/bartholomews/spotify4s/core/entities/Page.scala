package io.bartholomews.spotify4s.core.entities

import sttp.model.Uri

/**
  * href 	string 	A link to the Web API endpoint returning the full result of the request.
  * items 	an array of objects 	The requested data.
  * limit 	integer 	The maximum number of items in the response (as set in the query or by default).
  * next 	string 	URL to the next page of items. ( null if none)
  * offset 	integer 	The offset of the items returned (as set in the query or by default).
  * previous 	string 	URL to the previous page of items. ( null if none)
  * total 	integer 	The maximum number of items available to return.
  *
  * @tparam A the page items type
  */
case class Page[A](
  href: Uri,
  items: List[A],
  limit: Option[Int],
  next: Option[String],
  offset: Option[Int],
  previous: Option[String],
  total: Int
)
