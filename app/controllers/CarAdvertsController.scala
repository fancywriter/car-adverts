package controllers

import java.util.UUID

import dao.CarAdvertsDao
import formats.CarAdvertFormats._
import models.CarAdvert
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent._

class CarAdvertsController(dao: CarAdvertsDao, components: ControllerComponents)(implicit executionContext: ExecutionContext)
  extends AbstractController(components) {

  import components._

  def getAdverts(sort: String): Action[AnyContent] = actionBuilder.async {
    dao.getAdverts(sort).map(s => Ok(Json.toJson(s)))
  }

  def getAdvert(id: UUID): Action[AnyContent] = actionBuilder.async {
    dao.getAdvert(id).map(_.fold(NotFound("Not Found"))(a => Ok(Json.toJson(a))))
  }

  def createAdvert(): Action[JsValue] = actionBuilder.async(parsers.json) { request =>
    request.body.validate[CarAdvert].fold(badRequest, { a: CarAdvert =>
      dao.createAdvert(a).map(x => Ok(Json.toJson(x)))
    })
  }

  def modifyAdvert(id: UUID): Action[JsValue] = actionBuilder.async(parsers.json) { request =>
    request.body.validate[CarAdvert].fold(badRequest, { a: CarAdvert =>
      dao.modifyAdvert(id, a).map(x => Ok(Json.toJson(x)))
    })
  }

  def deleteAdvert(id: UUID): Action[AnyContent] = actionBuilder.async {
    dao.deleteAdvert(id).map(x => Ok(Json.toJson(x)))
  }

  private[this] def badRequest(errors: Seq[(JsPath, Seq[JsonValidationError])]) =
    Future.successful(BadRequest(JsError.toJson(errors)))

}
