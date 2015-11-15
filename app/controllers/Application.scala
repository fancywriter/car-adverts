package controllers

import java.util.UUID
import javax.inject.Inject

import dao.CarAdvertDao
import models.CarAdvert
import play.api.data.validation.ValidationError
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future

class Application @Inject()(dao: CarAdvertDao) extends Controller {

  def getAdverts(sort: String) = Action.async {
    dao.getAdverts(sort).map(s => Ok(Json.toJson(s)))
  }

  def getAdvert(id: UUID) = Action.async {
    dao.getAdvert(id).map(_.fold(NotFound("Not Found"))(a => Ok(Json.toJson(a))))
  }

  def createAdvert() = Action.async(BodyParsers.parse.json) { request =>
    request.body.validate[CarAdvert].fold(badRequest, { a: CarAdvert =>
      dao.createAdvert(a) map (x => Ok(Json.toJson(x)))
    })
  }

  def modifyAdvert(id: UUID) = Action.async(BodyParsers.parse.json) { request =>
    request.body.validate[CarAdvert].fold(badRequest, { a: CarAdvert =>
      dao.modifyAdvert(id, a) map (x => Ok(Json.toJson(x)))
    })
  }

  def deleteAdvert(id: UUID) = Action.async {
    dao.deleteAdvert(id) map (x => Ok(Json.toJson(x)))
  }

  private[this] def badRequest(errors: Seq[(JsPath, Seq[ValidationError])]) = Future(BadRequest(JsError.toJson(errors)))

}
