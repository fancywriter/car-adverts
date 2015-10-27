package controllers

import models.{CarAdvert, _}
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

import scala.util.Random

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  private class MyApp extends FakeApplication(additionalConfiguration = Map(
    "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
    "slick.dbs.default.db.driver" -> "org.h2.Driver",
    "slick.dbs.default.db.url" -> s"jdbc:h2:mem:play-test-${Random.nextInt()};DB_CLOSE_ON_EXIT=FALSE"
  ))

  "Application" should {

    "send 404 on a bad request" in new WithApplication(new MyApp) {
      route(FakeRequest(GET, "/boum")) must beSome.which(status(_) == NOT_FOUND)
    }

    "show empty list" in new WithApplication(new MyApp) {
      val adverts = route(FakeRequest(GET, "/adverts")).get
      status(adverts) must equalTo(OK)
      contentType(adverts) must beSome.which(_ == "application/json")
      contentAsJson(adverts).as[Seq[CarAdvert]] must beEmpty
    }
    
    "add new advert" in new WithApplication(new MyApp) {
      val advert = CarAdvert(None, "BMW", Fuel.Diesel, 0, `new` = false, None, None)
      val resp1 = route(FakeRequest(POST, "/adverts").withJsonBody(Json.toJson(advert))).get

      status(resp1) must equalTo(OK)
      // TODO ugly, replace in future
      Thread.sleep(100)

      val resp2 = route(FakeRequest(GET, "/adverts")).get
      status(resp2) must equalTo(OK)
      contentType(resp2) must beSome.which(_ == "application/json")
      contentAsJson(resp2).as[Seq[CarAdvert]] must not(beEmpty)
    }

  }
}
