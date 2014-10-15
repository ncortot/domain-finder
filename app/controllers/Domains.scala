package controllers

import javax.inject.Singleton
import play.api.mvc.Controller

import models.Domain

@Singleton
class Domains extends Controller with MongoAPI[Domain] {

  val collectionName = "domains"

  def create = createAction

  def delete(id: String) = deleteAction(id)

  def list = listAction

  def update(id: String) = updateAction(id)

}
