package actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.FSM
import akka.actor.Props
import play.libs.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import scala.concurrent.Future

import models.Domain
import models.Token

import GeneratorActor._

object GeneratorActor {

  lazy val generatorActor: ActorRef = Akka.system.actorOf(Props(classOf[GeneratorActor]))

  sealed trait State
  case object Idle extends State
  case object Active extends State
  case object Pending extends State

  sealed trait Data
  case object Empty extends Data

  case object Generate
  case object Completed

}

class GeneratorActor extends Actor with ActorLogging with FSM[State, Data] {

  startWith(Idle, Empty)

  when(Idle) {
    case Event(Generate, _) =>
      generateDomains.map { _ => self ! Completed }
      goto(Active)
  }

  when(Active) {
    case Event(Completed, _) =>
      goto(Idle)
    case Event(Generate, _) =>
      goto(Pending)
  }

  when(Pending) {
    case Event(Completed, _) =>
      self ! Generate
      goto(Idle)
    case Event(Generate, _) =>
      stay()
  }

  private def getDomains: Future[List[String]] = Domain.collection
    .find(Json.obj())
    .cursor[Domain]
    .collect[List]()
    .map { objects => objects map { _.name } }

  private def getTokens: Future[List[String]] = Token.collection
    .find(Json.obj())
    .cursor[Token]
    .collect[List]()
    .map { objects => objects map { _.value } }

  private def generate(tokenA: String, tokenB: String): List[String] =
    List(
      tokenA + ".com",
      tokenA + tokenB + ".com"
    )

  private def generate(tokens: List[String]): List[String] =
    for {
      tokenA <- tokens
      tokenB <- tokens
      domain <- generate(tokenA, tokenB)
    } yield domain

  private def generate: Future[List[String]] =
    for {
      knownDomains <- getDomains
      knownTokens <- getTokens
    } yield (generate(knownTokens).toSet -- knownDomains.toSet).toList

  private def generateDomains: Future[Unit] = {
    generate.flatMap {
      case Nil =>
        log.info("No new domain")
        Future.successful[Unit]()
      case newDomains =>
        val objects = newDomains.map {
          name => Domain(name = name, score = 0)
        }
        Domain.collection
          .bulkInsert(Enumerator.enumerate(objects))
          .map { lastError =>
            log.info(s"${newDomains.size} new domains generated")
          }
    }
  }

}

