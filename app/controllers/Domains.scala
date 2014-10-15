package controllers

import javax.inject.Singleton
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller

import models.Domain

@Singleton
class Domains extends Controller with MongoAPI[Domain] {

  val collectionName = "domains"

  def create = createAction

  def delete(id: String) = deleteAction(id)

  def list = listAction

  def updateFlag(id: String, flag: String, state: Boolean) = Action.async {
    collection
      .update(ObjectId(id), Json.obj(
        "$set" -> Json.obj(flag -> state)
      ))
      .map { lastError =>
        Ok(s"Object updated")
      }
  }

  def updateScore(id: String, delta: Int) = Action.async {
    collection
      .update(ObjectId(id), Json.obj(
        "$inc" -> Json.obj("score" -> delta)
      ))
      .map { lastError =>
        Ok(s"Object updated")
      }
  }

}
