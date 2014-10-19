package models

import play.api.Play
import play.api.Play.current
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB

object MongoDB {

  private def connection = ReactiveMongoPlugin.connection

  private lazy val dbName = Play.current.configuration.getString("mongodb.db").get

  private lazy val db = DB(dbName, connection)

  def ObjectId(id: String): JsObject = Json.obj("_id" -> Json.obj("$oid" -> id))

}

trait MongoDB {

  val collectionName: String

  def collection: JSONCollection = MongoDB.db.collection[JSONCollection](collectionName)

}
