package controllers

import java.time.LocalDate
import java.util.UUID

import models.{Fuel, CarAdvert}
import models.Fuel.Fuel
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._

class Application extends Controller {

  implicit val advertWrites: Writes[CarAdvert] = (
    (JsPath \ "id").writeNullable[UUID] and
      (JsPath \ "title").write[String] and
      (JsPath \ "fuel").write[Fuel] and
      (JsPath \ "price").write[Int] and
      (JsPath \ "new").write[Boolean] and
      (JsPath \ "mileage").writeNullable[Int] and
      (JsPath \ "firstRegistration").writeNullable[LocalDate]
    )(unlift(CarAdvert.unapply))

  implicit val fuelReads = new Reads[Fuel] {
    override def reads(json: JsValue) = json match {
      case JsString(s) => JsSuccess(Fuel.withName(s))
      case _ => JsError("String value expected!")
    }
  }

  implicit val advertReads: Reads[CarAdvert] = (
    (JsPath \ "id").readNullable[UUID] and
      (JsPath \ "title").read[String] and
      (JsPath \ "fuel").read[Fuel] and
      (JsPath \ "price").read[Int] and
      (JsPath \ "new").read[Boolean] and
      (JsPath \ "mileage").readNullable[Int] and
      (JsPath \ "firstRegistration").readNullable[LocalDate]
    )(CarAdvert.apply _)

  def getAdverts(sort: String) = Action {
    // TODO add sorting
    Ok(Json.toJson(CarAdvert.adverts))
  }

  def getAdvert(id: UUID) = Action {
    CarAdvert.adverts.find(_.id == id).fold(NotFound("Not Found"))(a => Ok(Json.toJson(a)))
  }

  def createAdvert() = Action(BodyParsers.parse.json) { request =>
    request.body.validate[CarAdvert].fold(errors => BadRequest(JsError.toJson(errors)), { a: CarAdvert =>
      val a1 = CarAdvert(Some(UUID.randomUUID()), a.title, a.fuel, a.price, a.`new`, a.mileage, a.firstRegistration)
      CarAdvert.adverts += a1
      Ok(Json.toJson(a1))
    })
  }

  def modifyAdvert(id: UUID) = Action(BodyParsers.parse.json)  { request =>
    request.body.validate[CarAdvert].fold(errors => BadRequest(JsError.toJson(errors)), { a: CarAdvert =>
      CarAdvert.adverts.find(_.id == id).map(CarAdvert.adverts -= _)
      val a1 = CarAdvert(Some(id), a.title, a.fuel, a.price, a.`new`, a.mileage, a.firstRegistration)
      CarAdvert.adverts += a1
      Ok(Json.toJson(a1))
    })
  }

  def deleteAdvert(id: UUID) = Action {
    CarAdvert.adverts.find(_.id == id).fold(NotFound("Not Found")) { a =>
      CarAdvert.adverts -= a
      Ok(Json.toJson(a))
    }
  }

}
