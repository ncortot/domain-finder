package controllers

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc._

@Singleton
class Application @Inject() extends Controller {

  def index(jsRoute: String) = Action {
    Ok(views.html.index())
  }

}
