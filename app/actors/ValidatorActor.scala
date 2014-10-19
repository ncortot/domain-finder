package actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.FSM
import akka.actor.Props
import play.libs.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import scala.concurrent.Future
import scala.sys.process._

import models.Domain
import models.MongoDB

import ValidatorActor._

object ValidatorActor {

  lazy val validatorActor: ActorRef = Akka.system.actorOf(Props(classOf[ValidatorActor]))

  sealed trait State
  case object Idle extends State
  case object Active extends State
  case object Pending extends State

  sealed trait Data
  case object Empty extends Data

  case object Validate
  case object Completed

}

class ValidatorActor extends Actor with ActorLogging with FSM[State, Data] {

  startWith(Idle, Empty)

  when(Idle) {
    case Event(Validate, _) =>
      validateDomains.map { _ => self ! Completed }
      goto(Active)
  }

  when(Active) {
    case Event(Completed, _) =>
      goto(Idle)
    case Event(Validate, _) =>
      goto(Pending)
  }

  when(Pending) {
    case Event(Completed, _) =>
      self ! Validate
      goto(Idle)
    case Event(Validate, _) =>
      stay()
  }

  private def getDomains: Future[List[Domain]] = Domain.collection
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
        log.info(s"Domain ${domain.name} available")
        Some(true)
      case lines if lines.exists(notAvailable) =>
        log.info(s"Domain ${domain.name} not available")
        Some(false)
      case lines =>
        log.error(s"Error checking domain ${domain.name}")
        log.error("Lines: " + lines.mkString("\n"))
        None
    }
  }

  private def updateDomain(domain: Domain): Future[Unit] =
    isAvailable(domain)
      .map { available =>
        Domain.collection
          .update(MongoDB.ObjectId(domain._id.get.stringify), Json.obj(
            "$set" -> Json.obj("available" -> available)
          ))
          .map { lastError => () }
      }
      .getOrElse(Future.successful(()))

  private def validateDomains: Future[Unit] =
    getDomains
      .flatMap { domainList =>
        domainList.foldLeft(Future.successful(())) { (future, domain) =>
          future flatMap { _ => updateDomain(domain) }
        }
      }
      .map { _ =>
        log.info("Domains validated")
      }

}
