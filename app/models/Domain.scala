package models

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.modules.reactivemongo.json.BSONFormats.BSONObjectIDFormat
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType
import reactivemongo.bson.BSONObjectID

case class Domain(
  _id: Option[BSONObjectID] = None,
  name: String,
  score: Int,
  available: Option[Boolean] = None,
  available_at: Option[java.util.Date] = None,
  owned: Option[Boolean] = None,
  hidden: Option[Boolean] = None
)

object Domain extends MongoDB {

  val collectionName = "domains"

  collection.indexesManager.ensure(Index(
    Seq("name" -> IndexType.Ascending),
    unique = true))

  implicit val domainFormat = Json.format[Domain]

}
