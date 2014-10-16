package controllers

import javax.inject.Singleton
import play.api.mvc.Controller

import models.Token

@Singleton
class Tokens extends Controller with MongoAPI[Token] {

  protected def collection = Token.collection

  def create = createAction { token =>
    token.copy(value = token.value.toLowerCase)
  }

  def delete(id: String) = deleteAction(id)

  def list = listAction

}
