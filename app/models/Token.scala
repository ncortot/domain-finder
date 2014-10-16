package models

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.modules.reactivemongo.json.BSONFormats.BSONObjectIDFormat
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType
import reactivemongo.bson.BSONObjectID

case class Token(
  _id: Option[BSONObjectID] = None,
  value: String
)

object Token extends MongoDB {

  val collectionName = "tokens"

  collection.indexesManager.ensure(Index(
    Seq("value" -> IndexType.Ascending),
    unique = true))

  implicit val tokenFormat = Json.format[Token]

}
