package models

import play.api.Play
import play.api.Play.current
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB

object MongoDB {

  private def connection = ReactiveMongoPlugin.connection

  private lazy val dbName = Play.current.configuration.getString("mongodb.db").get

  private lazy val db = DB(dbName, connection)

}

trait MongoDB {

  val collectionName: String

  def collection: JSONCollection = MongoDB.db.collection[JSONCollection](collectionName)

}
