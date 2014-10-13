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

  def list = Action.async {
    val cursor: Cursor[Token] = collection
      .find(Json.obj())
      .sort(Json.obj("value" -> 1))
      .cursor[Token]
    val futureTokenList: Future[List[Token]] = cursor.collect[List]()
    val futureTokensJsonArray: Future[JsArray] = futureTokenList.map { tokens =>
      Json.arr(tokens)
    }
    futureTokensJsonArray.map { tokens => Ok(tokens(0)) }
  }

  def update(id: String) = Action.async(parse.json) { request =>
    request.body.validate[Token].map { token =>
      collection.update(ObjectId(id), token).map { lastError =>
        logger.debug(s"Successfully updated with LastError: $lastError")
        Ok(s"Token updated")
      }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

}
