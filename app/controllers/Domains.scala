package controllers

import javax.inject.Singleton
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.bulk
import scala.concurrent.Future

import models.Domain
import models.Token

@Singleton
class Domains extends Controller with MongoAPI[Domain] {

  val collectionName = Domain.collectionName

  def create = createAction

  def delete(id: String) = deleteAction(id)

  def list = listAction

  def updateFlag(id: String, flag: String, state: Boolean) = Action.async {
    flag match {
      case "owned" =>
      case "hidden" =>
      case _ => throw new IllegalArgumentException("Invalid flag \"" + flag + "\"")
    }
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

  private def getDomains: Future[List[String]] = collection
    .find(Json.obj())
    .cursor[Domain]
    .collect[List]()
    .map { objects => objects map { _.name } }

  private def getTokens: Future[List[String]] = db
    .collection[JSONCollection](Token.collectionName)
    .find(Json.obj())
    .cursor[Token]
    .collect[List]()
    .map { objects => objects map { _.value } }

  private def generateDomains(tokenA: String, tokenB: String): List[String] =
    List(tokenA + tokenB + ".com")

  private def generateDomains(tokens: List[String]): List[String] =
    for {
      tokenA <- tokens
      tokenB <- tokens
      domain <- generateDomains(tokenA, tokenB)
    } yield domain

  private def generateDomains: Future[List[String]] =
    for {
      knownDomains <- getDomains
      knownTokens <- getTokens
    } yield (generateDomains(knownTokens).toSet -- knownDomains.toSet).toList

  def generate = Action.async {
    generateDomains.flatMap {
      case Nil =>
        Future.successful(Ok("No new domain"))
      case newDomains =>
        val objects = newDomains.map { name => Domain(name = name, score = 0) }
        collection
          .bulkInsert(Enumerator.enumerate(objects))
          .map { lastError =>
            Ok(s"${newDomains.size} new domains generated")
          }
    }
  }

}
