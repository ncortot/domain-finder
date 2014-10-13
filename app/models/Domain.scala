package models

import play.api.libs.json.Json
import play.modules.reactivemongo.json.BSONFormats.BSONObjectIDFormat
import reactivemongo.bson.BSONObjectID

case class Domain(
  _id: Option[BSONObjectID] = None,
  value: String
)

object Domain {

  implicit val domainFormat = Json.format[Domain]

}
