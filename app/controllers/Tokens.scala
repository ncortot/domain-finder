package controllers

import javax.inject.Singleton
import play.api.mvc.Controller

import models.Token

@Singleton
class Tokens extends Controller with MongoAPI[Token] {

  val collectionName = Token.collectionName

  def create = createAction

  def delete(id: String) = deleteAction(id)

  def list = listAction

  def update(id: String) = updateAction(id)

}
