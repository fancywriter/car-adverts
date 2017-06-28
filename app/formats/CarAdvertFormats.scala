package formats

import models._
import play.api.libs.json._

object CarAdvertFormats {
  implicit val fuelFormat = new Format[Fuel] {
    override def reads(json: JsValue): JsResult[Fuel] = json.validate[String].flatMap { s =>
      Fuel.get(s).fold(JsError(s"Unknown fuel '$s'"): JsResult[Fuel])(JsSuccess(_))
    }
    override def writes(o: Fuel): JsValue = JsString(o.toString.toLowerCase)
  }

  implicit val advertFormat: Format[CarAdvert] = {
    val advertReads: Reads[CarAdvert] = Json.reads[CarAdvert]
    val advertReadsValid = new Reads[CarAdvert] {
      override def reads(json: JsValue): JsResult[CarAdvert] = json.validate[CarAdvert](advertReads)
        .filter(JsError("'mileage' and 'firstRegistration' can be defined only for used cars")) {
          a => !a.`new` || (a.mileage.isEmpty && a.firstRegistration.isEmpty)
        }
    }
    Format[CarAdvert](advertReadsValid, Json.writes[CarAdvert])
  }
}
