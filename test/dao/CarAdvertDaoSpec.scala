package dao

import java.time.LocalDate

import models.{CarAdvert, Fuel}
import org.scalatest.enablers.Sortable
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

    "sort by price" in {
      val advert1 = CarAdvert(None, "BMW", Fuel.Gasoline, 1000, `new` = false, None, None)
      val advert2 = CarAdvert(None, "Skoda", Fuel.Gasoline, 2000, `new` = false, None, None)

      val f1 = dao.createAdvert(advert1)
      val f2 = dao.createAdvert(advert2)

      val f3 = for {
        _ <- f1
        _ <- f2
        x <- dao.getAdverts("price")
      } yield x

      implicit val sortable = new Sortable[Seq[CarAdvert]] {
        override def isSorted(seq: Seq[CarAdvert]) = (seq, seq.tail).zipped.forall(_.price <= _.price)
      }

      whenReady(f3) { adverts => adverts mustBe sorted }
    }

    "sort by mileage" in {
      val advert1 = CarAdvert(None, "BMW", Fuel.Gasoline, 1000, `new` = false, Some(10000), None)
      val advert2 = CarAdvert(None, "Skoda", Fuel.Gasoline, 2000, `new` = false, Some(5000), None)

      val f1 = dao.createAdvert(advert1)
      val f2 = dao.createAdvert(advert2)

      val f3 = for {
        _ <- f1
        _ <- f2
        x <- dao.getAdverts("mileage")
      } yield x

      implicit val sortable = new Sortable[Seq[CarAdvert]] {
        override def isSorted(seq: Seq[CarAdvert]) = (seq, seq.tail).zipped.forall(_.mileage.value <= _.mileage.value)
      }

      whenReady(f3) ( adverts => adverts mustBe sorted )
    }

    "sort by first registration" in {
      val advert1 = CarAdvert(None, "BMW", Fuel.Gasoline, 1000, `new` = false, None, Some(LocalDate.of(2015, 1, 1)))
      val advert2 = CarAdvert(None, "Skoda", Fuel.Gasoline, 2000, `new` = false, None, Some(LocalDate.of(2015, 2, 2)))

      val f1 = dao.createAdvert(advert1)
      val f2 = dao.createAdvert(advert2)

      val f3 = for {
        _ <- f1
        _ <- f2
        x <- dao.getAdverts("firstRegistration")
      } yield x

      implicit val sortable = new Sortable[Seq[CarAdvert]] {
        override def isSorted(seq: Seq[CarAdvert]) = (seq, seq.tail).zipped
          .forall((a1, a2) => a1.firstRegistration.value.compareTo(a2.firstRegistration.value) <= 0)
      }

      whenReady(f3) { adverts => adverts mustBe sorted }
    }

  }

}
