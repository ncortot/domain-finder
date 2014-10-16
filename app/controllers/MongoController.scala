package controllers

import play.api.Play
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.__
import play.api.mvc.Controller
import play.modules.reactivemongo.{ MongoController => ReactiveMongoController }
import reactivemongo.api.DB

/** Add authentication database support to the MongoController.
  */
trait MongoController extends ReactiveMongoController {
  self: Controller =>

  private lazy val dbName = Play.current.configuration.getString("mongodb.db").get

  override lazy val db = DB(dbName, connection)

  protected val fromObjectId = __.json.update(
    (__ \ '_id).json.copyFrom((__ \ '_id \ '$oid).json.pick)
  )

  protected def ObjectId(id: String): JsObject = Json.obj("_id" -> Json.obj("$oid" -> id))

}
