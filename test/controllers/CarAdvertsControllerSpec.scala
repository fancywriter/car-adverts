package controllers

import java.util.UUID

import formats.CarAdvertFormats._
import models._
import org.scalatest.MustMatchers._
import org.scalatest.OptionValues._
import org.scalatest.WordSpec
import org.scalatestplus.play._
import play.api.http.MimeTypes
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import utils.AppFactory

class CarAdvertsControllerSpec extends WordSpec with AppFactory with BaseOneAppPerTest with WsScalaTestClient {

  "Application" should {

    "send 404 on not existed path" in {
      val resp = route(app, FakeRequest(GET, "/boum")).value
      status(resp) mustBe NOT_FOUND
    }

    "show empty list" in {
      val resp = route(app, FakeRequest(GET, "/adverts")).value
      status(resp) mustBe OK
      contentType(resp).value mustBe MimeTypes.JSON
      contentAsJson(resp).as[Seq[CarAdvert]] mustBe empty
    }

    "add new advert" in {
      val advert = CarAdvert(None, "BMW", Fuel.Diesel, 0, `new` = false, None, None)
      val resp1 = route(app, FakeRequest(POST, "/adverts").withJsonBody(Json.toJson(advert))).value
      status(resp1) mustBe OK

      val resp2 = route(app, FakeRequest(GET, "/adverts")).value
      status(resp2) mustBe OK
      contentType(resp2).value mustBe MimeTypes.JSON
      contentAsJson(resp2).as[Seq[CarAdvert]] mustNot be(empty)
    }

    "get advert by id" in {
      val advert = CarAdvert(None, "BMW", Fuel.Diesel, 0, `new` = false, None, None)
      val resp1 = route(app, FakeRequest(POST, "/adverts").withJsonBody(Json.toJson(advert))).value
      status(resp1) mustBe OK

      val id = contentAsJson(resp1).as[CarAdvert].id.value
      val resp2 = route(app, FakeRequest(GET, "/adverts/" + id)).value
      status(resp2) mustBe OK
      contentType(resp2).value mustBe MimeTypes.JSON
      contentAsJson(resp2).as[CarAdvert] must not be null
    }

    "delete advert" in {
      val advert = CarAdvert(None, "BMW", Fuel.Diesel, 0, `new` = false, None, None)
      val resp1 = route(app, FakeRequest(POST, "/adverts").withJsonBody(Json.toJson(advert))).value
      status(resp1) mustBe OK

      val id = contentAsJson(resp1).as[CarAdvert].id.get
      val resp2 = route(app, FakeRequest(DELETE, "/adverts/" + id)).value
      status(resp2) mustBe OK

      val resp3 = route(app, FakeRequest(GET, "/adverts/" + id)).value
      status(resp3) mustBe NOT_FOUND
    }

    "modify advert" in {
      val advert = CarAdvert(None, "BMW", Fuel.Diesel, 0, `new` = false, None, None)
      val resp1 = route(app, FakeRequest(POST, "/adverts").withJsonBody(Json.toJson(advert))).value
      status(resp1) mustBe OK

      val id = contentAsJson(resp1).as[CarAdvert].id.get
      val newAdvert = advert.copy(title = "Skoda")
      val resp2 = route(app, FakeRequest(PUT, "/adverts/" + id).withJsonBody(Json.toJson(newAdvert))).value
      status(resp2) mustBe OK

      val resp3 = route(app, FakeRequest(GET, "/adverts/" + id)).value
      status(resp3) mustBe OK
      contentType(resp3).value mustBe MimeTypes.JSON
      contentAsJson(resp3).as[CarAdvert].title mustBe newAdvert.title
    }

    "throw bad request for empty body" in {
      val resp1 = route(app, FakeRequest(POST, "/adverts").withJsonBody(Json.obj())).value
      status(resp1) mustBe BAD_REQUEST

      val resp2 = route(app, FakeRequest(PUT, "/adverts/" + UUID.randomUUID()).withJsonBody(Json.obj())).value
      status(resp2) mustBe BAD_REQUEST
    }

    "throw bad request for unknown fuel" in {
      val body = Json.obj("title" -> "BMW", "fuel" -> "Water", "price" -> 10000, "new" -> false)
      val resp = route(app, FakeRequest(POST, "/adverts").withJsonBody(body)).value
      status(resp) mustBe BAD_REQUEST
    }

    "throw bad request for nonempty mileage on new cars" in {
      val body = Json.obj("title" -> "BMW", "fuel" -> "Gasoline", "price" -> 10000, "new" -> true, "mileage" -> 1000)
      val resp = route(app, FakeRequest(POST, "/adverts").withJsonBody(body)).value
      status(resp) mustBe BAD_REQUEST
    }

    "throw bad request for nonempty registration on new cars" in {
      val body = Json.obj("title" -> "BMW", "fuel" -> "Gasoline", "price" -> 10000, "new" -> true, "firstRegistration" -> "2015-01-01")
      val resp = route(app, FakeRequest(POST, "/adverts").withJsonBody(body)).value
      status(resp) mustBe BAD_REQUEST
    }
  }
}
