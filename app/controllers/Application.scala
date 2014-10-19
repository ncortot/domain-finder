package controllers

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc._

import actors.GeneratorActor
import actors.ValidatorActor

@Singleton
class Application @Inject() extends Controller {

  def index(jsRoute: String) = Action {
    Ok(views.html.index())
  }

  def generate = Action {
    GeneratorActor.generatorActor ! GeneratorActor.Generate
    Ok("started")
  }

  def validate = Action {
    ValidatorActor.validatorActor ! ValidatorActor.Validate
    Ok("started")
  }

}
