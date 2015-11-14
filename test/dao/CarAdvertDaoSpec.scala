package dao

import models.{CarAdvert, Fuel}
import org.scalatest.time.{Millis, Seconds, Span}
import play.api.Application
import utils.BaseSpec

import scala.concurrent.ExecutionContext.Implicits.global

class CarAdvertDaoSpec extends BaseSpec {

  implicit val patience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  def dao(implicit app: Application) = app.injector.instanceOf[CarAdvertDao]

  "CarAdvertDao" should {

    "return empty list" in {
      whenReady(dao.getAdverts("title"))(adverts => adverts mustBe empty)
    }

    "create new advert" in {
      val advert = CarAdvert(None, "title", Fuel.Gasoline, 1000, `new` = false, None, None)
      val f = dao.createAdvert(advert).flatMap(_ => dao.getAdverts("title"))
      whenReady(f)(adverts => adverts must have length 1)
    }

    "get advert by id" in {
      val advert = CarAdvert(None, "title", Fuel.Gasoline, 1000, `new` = false, None, None)
      val f = dao.createAdvert(advert).flatMap(a => dao.getAdvert(a.id.get))
      whenReady(f)(opt => opt.value.title mustEqual advert.title)
    }

    "delete advert" in {
      val advert = CarAdvert(None, "title", Fuel.Gasoline, 1000, `new` = false, None, None)
      val f = dao.createAdvert(advert)
        .flatMap(a => dao.deleteAdvert(a.id.get))
        .flatMap(_ => dao.getAdverts("title"))
      whenReady(f) { adverts => adverts must be(empty) }
    }

    "modify advert" in {
      val oldAdvert = CarAdvert(None, "oldtitle", Fuel.Gasoline, 1000, `new` = false, None, None)
      val newAdvert = oldAdvert.copy(title = "newtitle")
      val f = dao.createAdvert(oldAdvert).flatMap { a =>
        dao.modifyAdvert(a.id.get, newAdvert)
          .flatMap(_ => dao.getAdvert(a.id.get))
      }
      whenReady(f)(opt => opt.value.title mustEqual newAdvert.title)
    }

  }

}
