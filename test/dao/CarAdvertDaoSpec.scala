package dao

import models.{CarAdvert, Fuel}
import org.junit.runner.RunWith
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.Application
import play.api.test.WithApplication
import utils.TestApp

import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class CarAdvertDaoSpec extends Specification with ScalaFutures {

  implicit val patience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  def dao(implicit app: Application) = app.injector.instanceOf[CarAdvertDao]

  "CarAdvertDao" should {

    "return empty list" in new WithApplication(new TestApp) {
      whenReady(dao.getAdverts("title"))(adverts => adverts must beEmpty)
    }

    "create new advert" in new WithApplication(new TestApp) {
      val advert = CarAdvert(None, "title", Fuel.Gasoline, 1000, `new` = false, None, None)
      val f = dao.createAdvert(advert).flatMap(_ => dao.getAdverts("title"))
      whenReady(f)(adverts => adverts must haveLength(1))
    }

    "get advert by id" in new WithApplication(new TestApp) {
      val advert = CarAdvert(None, "title", Fuel.Gasoline, 1000, `new` = false, None, None)
      val f = dao.createAdvert(advert).flatMap(a => dao.getAdvert(a.id.get))
      whenReady(f)(opt => opt must beSome[CarAdvert].which(_.title == advert.title))
    }.pendingUntilFixed

    "delete advert" in new WithApplication(new TestApp) {
      val advert = CarAdvert(None, "title", Fuel.Gasoline, 1000, `new` = false, None, None)
      val f = dao.createAdvert(advert)
        .flatMap(a => dao.deleteAdvert(a.id.get))
        .flatMap(_ => dao.getAdverts("title"))
      whenReady(f) { adverts => adverts must beEmpty }
    }.pendingUntilFixed

    "modify advert" in new WithApplication(new TestApp) {
      val oldAdvert = CarAdvert(None, "oldtitle", Fuel.Gasoline, 1000, `new` = false, None, None)
      val newAdvert = oldAdvert.copy(title = "newtitle")
      val f = dao.createAdvert(oldAdvert).flatMap { a =>
        dao.modifyAdvert(a.id.get, newAdvert)
          .flatMap(_ => dao.getAdvert(a.id.get))
      }
      whenReady(f)(opt => opt must beSome[CarAdvert].which(_.title == newAdvert.title))
    }.pendingUntilFixed

  }

}
