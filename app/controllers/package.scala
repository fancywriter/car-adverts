import java.time.LocalDate
import java.util.UUID

import models.{Fuel, CarAdvert}
import models.Fuel._
import play.api.libs.functional.syntax._
import play.api.libs.json._

package object controllers {

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

}
