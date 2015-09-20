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

  implicit val advertReads = new Reads[CarAdvert]() {
    override def reads(json: JsValue) = {
      val id = (json \ "id").asOpt[String].map(UUID.fromString)
      val title = (json \ "title").as[String]
      val fuel = Fuel.withName((json \ "fuel").as[String])
      val price = (json \ "price").as[Int]
      val `new` = (json \ "new").as[Boolean]
      val mileage = (json \ "mileage").asOpt[Int]
      val firstRegistration = (json \ "firstRegistration").asOpt[LocalDate]
      if (`new` && (mileage.isDefined || firstRegistration.isDefined))
        JsError("'mileage' and 'firstRegistration' can be defined only for used cars!")
      else
        JsSuccess(CarAdvert(id, title, fuel, price, `new`, mileage, firstRegistration))
    }
  }

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
