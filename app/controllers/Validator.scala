package controllers

import javax.inject.Singleton
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import scala.sys.process._

import models.Domain

@Singleton
class Validator extends Controller with MongoController {

  private def getDomains: Future[List[Domain]] = db
    .collection[JSONCollection](Domain.collectionName)
    .find(Json.obj(
      "available" -> Json.obj("$exists" -> false)
    ))
    .cursor[Domain]
    .collect[List]()

  private def notAvailable(line: String): Boolean =
    line.startsWith("Registrant") ||
    line.startsWith("  Registrant") ||
    line.startsWith("   Registrar")

  private def isAvailable(domain: Domain): Option[Boolean] = {
    val output = new StringBuffer
    s"whois -H ${domain.name}".run(BasicIO(false, output, None)).exitValue()
    output.toString.split('\n') match {
      case lines if lines.exists(_.startsWith("No match for ")) =>
        println(s"Domain ${domain.name} available")
        Some(true)
      case lines if lines.exists(notAvailable) =>
        println(s"Domain ${domain.name} not available")
        Some(false)
      case lines =>
        println(s"Error checking domain ${domain.name}")
        println("Lines: " + lines.mkString("\n"))
        None
    }
  }

  private def updateDomain(collection: JSONCollection, domain: Domain): Future[Unit] =
    isAvailable(domain)
      .map { available =>
        collection
          .update(ObjectId(domain._id.get.stringify), Json.obj(
            "$set" -> Json.obj("available" -> available)
          ))
          .map { lastError => () }
      }
      .getOrElse(Future.successful(()))

  def validate = Action.async {
    val collection = db.collection[JSONCollection](Domain.collectionName)
    getDomains
      .flatMap { domainList =>
        domainList.foldLeft(Future.successful(())) { (future, domain) =>
          future flatMap { _ => updateDomain(collection, domain) }
        }
      }
      .map { _ =>
        Ok("Domains validated")
      }
  }

}
