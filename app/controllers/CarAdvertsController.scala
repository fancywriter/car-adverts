package controllers

import java.util.UUID
import javax.inject.Inject

import dao.CarAdvertsDao
import models.CarAdvert
import play.api.libs.json._
import play.api.mvc._
import formats.CarAdvertFormats._

import scala.concurrent.{ExecutionContext, Future}

class CarAdvertsController @Inject()(dao: CarAdvertsDao, components: ControllerComponents)(implicit executionContext: ExecutionContext)
  extends AbstractController(components) {

  def getAdverts(sort: String): Action[AnyContent] = Action.async {
    dao.getAdverts(sort).map(s => Ok(Json.toJson(s)))
  }

  def getAdvert(id: UUID): Action[AnyContent] = Action.async {
    dao.getAdvert(id).map(_.fold(NotFound("Not Found"))(a => Ok(Json.toJson(a))))
  }

  def createAdvert(): Action[JsValue] = Action.async(components.parsers.json) { request =>
    request.body.validate[CarAdvert].fold(badRequest, { a: CarAdvert =>
      dao.createAdvert(a) map (x => Ok(Json.toJson(x)))
    })
  }

  def modifyAdvert(id: UUID): Action[JsValue] = Action.async(components.parsers.json) { request =>
    request.body.validate[CarAdvert].fold(badRequest, { a: CarAdvert =>
      dao.modifyAdvert(id, a) map (x => Ok(Json.toJson(x)))
    })
  }

  def deleteAdvert(id: UUID): Action[AnyContent] = Action.async {
    dao.deleteAdvert(id) map (x => Ok(Json.toJson(x)))
  }

  private[this] def badRequest(errors: Seq[(JsPath, Seq[JsonValidationError])]) =
    Future.successful(BadRequest(JsError.toJson(errors)))

}
