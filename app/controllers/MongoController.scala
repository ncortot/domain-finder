package controllers

import play.api.Play
import play.api.mvc.Controller
import play.modules.reactivemongo.{ MongoController => ReactiveMongoController }
import reactivemongo.api.DB

/** Add authentication database support to the MongoController.
  */
trait MongoController extends ReactiveMongoController {
  self: Controller =>

  private lazy val dbName = Play.current.configuration.getString("mongodb.db").get

  override lazy val db = DB(dbName, connection)

}
