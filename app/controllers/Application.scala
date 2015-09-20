package controllers

import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

import db.CarAdvertDb
import models.Fuel.Fuel
import models.{CarAdvert, Fuel}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._

class Application @Inject()(db: CarAdvertDb) extends Controller {

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
    Ok(Json.toJson(db.getAdverts))
  }

  def getAdvert(id: UUID) = Action {
    db.getAdvert(id).fold(NotFound("Not Found"))(a => Ok(Json.toJson(a)))
  }

  def createAdvert() = Action(BodyParsers.parse.json) { request =>
    request.body.validate[CarAdvert].fold(errors => BadRequest(JsError.toJson(errors)), { a: CarAdvert =>
      Ok(Json.toJson(db.createAdvert(a)))
    })
  }

  def modifyAdvert(id: UUID) = Action(BodyParsers.parse.json) { request =>
    request.body.validate[CarAdvert].fold(errors => BadRequest(JsError.toJson(errors)), { a: CarAdvert =>
      db.modifyAdvert(id, a)
      Ok(Json.toJson(""))
    })
  }

  def deleteAdvert(id: UUID) = Action {
    db.deleteAdvert(id)
    Ok(Json.toJson(""))
  }

}
