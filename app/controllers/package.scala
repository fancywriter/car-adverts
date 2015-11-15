import java.time.LocalDate
import java.util.UUID

import models.Fuel._
import models.{CarAdvert, Fuel}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
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
    ) (unlift(CarAdvert.unapply))

  implicit val fuelReads = new Reads[Fuel] {
    override def reads(json: JsValue) = {
      json.validate[String].flatMap { s =>
        Fuel.values.find(_.toString.equalsIgnoreCase(s)).map(JsSuccess(_)).getOrElse(JsError(s"Unknown fuel '$s'"))
      }
    }
  }

  implicit val advertReadsValid = new Reads[CarAdvert] {

    val advertReads: Reads[CarAdvert] = (
      (JsPath \ "id").readNullable[UUID] and
        (JsPath \ "title").read[String] and
        (JsPath \ "fuel").read[Fuel] and
        (JsPath \ "price").read[Int] and
        (JsPath \ "new").read[Boolean] and
        (JsPath \ "mileage").readNullable[Int] and
        (JsPath \ "firstRegistration").readNullable[LocalDate]
      ) (CarAdvert.apply _)

    override def reads(json: JsValue): JsResult[CarAdvert] = {
      json.validate[CarAdvert](advertReads)
        .filter(JsError("'mileage' and 'firstRegistration' can be defined only for used cars")) {
          a => !a.`new` || (a.mileage.isEmpty && a.firstRegistration.isEmpty)
        }
    }
  }

}
