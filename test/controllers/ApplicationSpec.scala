package controllers

import models._
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.http.MimeTypes
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import utils.TestApp

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a not found" in new WithApplication(new TestApp) {
      route(FakeRequest(GET, "/boum")) must beSome.which(status(_) == NOT_FOUND)
    }

    "show empty list" in new WithApplication(new TestApp) {
      val adverts = route(FakeRequest(GET, "/adverts")).get
      status(adverts) must equalTo(OK)
      contentType(adverts) must beSome.which(_ == MimeTypes.JSON)
      contentAsJson(adverts).as[Seq[CarAdvert]] must beEmpty
    }

    "add new advert" in new WithApplication(new TestApp) {
      val advert = CarAdvert(None, "BMW", Fuel.Diesel, 0, `new` = false, None, None)
      val resp1 = route(FakeRequest(POST, "/adverts").withJsonBody(Json.toJson(advert))).get
      status(resp1) must equalTo(OK)

      val resp2 = route(FakeRequest(GET, "/adverts")).get
      status(resp2) must equalTo(OK)
      contentType(resp2) must beSome.which(_ == MimeTypes.JSON)
      contentAsJson(resp2).as[Seq[CarAdvert]] must not(beEmpty)
    }

    "get advert by id" in new WithApplication(new TestApp) {
      val advert = CarAdvert(None, "BMW", Fuel.Diesel, 0, `new` = false, None, None)
      val resp1 = route(FakeRequest(POST, "/adverts").withJsonBody(Json.toJson(advert))).get
      status(resp1) must equalTo(OK)

      val id = contentAsJson(resp1).as[CarAdvert].id.get
      val resp2 = route(FakeRequest(GET, "/adverts/" + id)).get
      status(resp2) must equalTo(OK)
      contentType(resp2) must beSome.which(_ == MimeTypes.JSON)
      contentAsJson(resp2).as[CarAdvert] must not(beNull)
    }

    "delete advert" in new WithApplication(new TestApp) {
      val advert = CarAdvert(None, "BMW", Fuel.Diesel, 0, `new` = false, None, None)
      val resp1 = route(FakeRequest(POST, "/adverts").withJsonBody(Json.toJson(advert))).get
      status(resp1) must equalTo(OK)

      val id = contentAsJson(resp1).as[CarAdvert].id.get
      val resp2 = route(FakeRequest(DELETE, "/adverts/" + id)).get
      status(resp2) must equalTo(OK)

      val resp3 = route(FakeRequest(GET, "/adverts/" + id)).get
      status(resp3) must equalTo(NOT_FOUND)
    }

    "modify advert" in new WithApplication(new TestApp) {
      val advert = CarAdvert(None, "BMW", Fuel.Diesel, 0, `new` = false, None, None)
      val resp1 = route(FakeRequest(POST, "/adverts").withJsonBody(Json.toJson(advert))).get
      status(resp1) must equalTo(OK)

      val id = contentAsJson(resp1).as[CarAdvert].id.get
      val newAdvert = advert.copy(title = "Skoda")
      val resp2 = route(FakeRequest(PUT, "/adverts/" + id).withJsonBody(Json.toJson(newAdvert))).get
      status(resp2) must equalTo(OK)

      val resp3 = route(FakeRequest(GET, "/adverts/" + id)).get
      status(resp3) must equalTo(OK)
      contentType(resp3) must beSome.which(_ == MimeTypes.JSON)
      contentAsJson(resp3).as[CarAdvert].title mustEqual newAdvert.title
    }
  }
}
