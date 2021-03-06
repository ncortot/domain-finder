package controllers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future

import models.MongoDB

trait MongoAPI[T] {
  self: Controller =>

  protected def collection: JSONCollection

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Tokens])

  protected val fromObjectId = __.json.update(
    (__ \ '_id).json.copyFrom((__ \ '_id \ '$oid).json.pick)
  )

  protected def createAction(f: T => T)(implicit rds: Reads[T], tjs: Writes[T]) =
    Action.async(parse.json) { request =>
      request.body.validate[T]
        .map(f)
        .map { obj =>
          collection.insert[T](obj).map { lastError =>
            logger.debug(s"Successfully inserted with LastError: $lastError")
            Created(s"Object created")
          }
        }
        .getOrElse(Future.successful(BadRequest("invalid json")))
    }

  protected def deleteAction(id: String) = Action.async {
    collection
      .remove(MongoDB.ObjectId(id))
      .map { lastError => Ok }
  }

  protected def listAction = Action.async {
    collection
      .find(Json.obj())
      .sort(Json.obj("value" -> 1))
      .cursor[JsObject]
      .collect[List]()
      .map { objects => objects map { _.transform(fromObjectId).get } }
      .map { x => Ok(Json.toJson(x)) }
  }

  protected def updateAction(id: String)(implicit rds: Reads[T], tjs: Writes[T]) =
    Action.async(parse.json) { request =>
      request.body.validate[T]
        .map { token =>
          collection
            .update(MongoDB.ObjectId(id), token)
            .map { lastError =>
              Ok(s"Object updated")
            }
        }
        .getOrElse(Future.successful(BadRequest("invalid json")))
    }

}
