package controllers

import javax.inject.Singleton
import org.slf4j.{LoggerFactory, Logger}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import reactivemongo.api.Cursor

import models._

@Singleton
class Tokens extends Controller with MongoController {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Tokens])

  val fromObjectId = __.json.update((__ \ '_id).json.copyFrom((__ \ '_id \ '$oid).json.pick))

  def collection: JSONCollection = db.collection[JSONCollection]("tokens")

  def ObjectId(id: String): JsObject = Json.obj("_id" -> Json.obj("$oid" -> id))

  def create = Action.async(parse.json) { request =>
    request.body.validate[Token].map { token =>
      collection.insert(token).map { lastError =>
        logger.debug(s"Successfully inserted with LastError: $lastError")
        Created(s"Token Created")
      }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def delete(id: String) = Action.async {
    collection
      .remove(ObjectId(id))
      .map { lastError => Ok }
  }

  def list = Action.async {
    collection
      .find(Json.obj())
      .sort(Json.obj("value" -> 1))
      .cursor[JsObject]
      .collect[List]()
      .map { mongoTokens =>
        val apiTokens = mongoTokens map { _.transform(fromObjectId).get }
        Ok(Json.toJson(apiTokens))
      }
  }

  def update(id: String) = Action.async(parse.json) { request =>
    request.body.validate[Token].map { token =>
      collection
        .update(ObjectId(id), token)
        .map { lastError =>
          Ok(s"Token updated")
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

}
