package controllers

import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.__

trait MongoHelpers {

  protected val fromObjectId = __.json.update(
    (__ \ '_id).json.copyFrom((__ \ '_id \ '$oid).json.pick)
  )

  protected def ObjectId(id: String): JsObject = Json.obj("_id" -> Json.obj("$oid" -> id))

}
