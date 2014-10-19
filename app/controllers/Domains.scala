package controllers

import javax.inject.Singleton
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller

import models.Domain
import models.MongoDB

@Singleton
class Domains extends Controller with MongoAPI[Domain] {

  protected def collection = Domain.collection

  def create = createAction { domain =>
    domain.copy(name = domain.name.toLowerCase)
  }

  def delete(id: String) = deleteAction(id)

  def list = listAction

  def updateFlag(id: String, flag: String, state: Boolean) = Action.async {
    flag match {
      case "owned" =>
      case "hidden" =>
      case _ => throw new IllegalArgumentException("Invalid flag \"" + flag + "\"")
    }
    collection
      .update(MongoDB.ObjectId(id), Json.obj(
        "$set" -> Json.obj(flag -> state)
      ))
      .map { lastError =>
        Ok(s"Object updated")
      }
  }

  def updateScore(id: String, delta: Int) = Action.async {
    collection
      .update(MongoDB.ObjectId(id), Json.obj(
        "$inc" -> Json.obj("score" -> delta)
      ))
      .map { lastError =>
        Ok(s"Object updated")
      }
  }

}
