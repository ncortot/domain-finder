package controllers

import javax.inject.Singleton
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future

import models.Domain
import models.Token

@Singleton
class Generator extends Controller with MongoController {

  private def getDomains: Future[List[String]] = db
    .collection[JSONCollection](Domain.collectionName)
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
        val collection = db.collection[JSONCollection](Domain.collectionName)
        val objects = newDomains.map { name => Domain(name = name, score = 0) }
        collection
          .bulkInsert(Enumerator.enumerate(objects))
          .map { lastError =>
            Ok(s"${newDomains.size} new domains generated")
          }
    }
  }

}
