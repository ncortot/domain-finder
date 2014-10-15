package models

import play.api.libs.json.Json
import play.modules.reactivemongo.json.BSONFormats.BSONObjectIDFormat
import reactivemongo.bson.BSONObjectID

case class Token(
  _id: Option[BSONObjectID] = None,
  value: String
)

object Token {

  val collectionName = "tokens"

  implicit val tokenFormat = Json.format[Token]

}
