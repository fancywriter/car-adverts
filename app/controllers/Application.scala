package controllers

import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

import dao.CarAdvertDao
import models.Fuel.Fuel
import models.{CarAdvert, Fuel}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

class Application @Inject()(dao: CarAdvertDao) extends Controller {

  def getAdverts(sort: String) = Action.async {
    dao.getAdverts(sort).map(s => Ok(Json.toJson(s)))
  }

  def getAdvert(id: UUID) = Action.async {
    dao.getAdvert(id).map(_.fold(NotFound("Not Found"))(a => Ok(Json.toJson(a))))
  }

  def createAdvert() = Action(BodyParsers.parse.json) { request =>
    request.body.validate[CarAdvert].fold(errors => BadRequest(JsError.toJson(errors)), { a: CarAdvert =>
      Ok(Json.toJson(dao.createAdvert(a)))
    })
  }

  def modifyAdvert(id: UUID) = Action(BodyParsers.parse.json) { request =>
    request.body.validate[CarAdvert].fold(errors => BadRequest(JsError.toJson(errors)), { a: CarAdvert =>
      dao.modifyAdvert(id, a)
      Ok(Json.toJson(""))
    })
  }

  def deleteAdvert(id: UUID) = Action {
    dao.deleteAdvert(id)
    Ok(Json.toJson(""))
  }

}
