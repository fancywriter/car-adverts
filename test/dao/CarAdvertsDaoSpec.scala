package dao

import java.time.LocalDate

import models.{CarAdvert, Fuel}
import org.scalatest.MustMatchers._
import org.scalatest.OptionValues._
import org.scalatest.WordSpec
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.enablers.Sortable
import org.scalatestplus.play.BaseOneAppPerTest
import play.api.Application
import utils.AppFactory

import scala.concurrent.ExecutionContext.Implicits.global

class CarAdvertsDaoSpec extends WordSpec with ScalaFutures with IntegrationPatience with AppFactory with BaseOneAppPerTest {

  // TODO instantiate Dao in test directly
  def dao(implicit app: Application): CarAdvertsDao = app.injector.instanceOf[CarAdvertsDao]

  "CarAdvertDao" should {

    "return empty list" in {
      val f = dao.getAdverts("title")
      f.futureValue mustBe empty
    }

    "create new advert" in {
      val advert = CarAdvert(None, "title", Fuel.Gasoline, 1000, `new` = false, None, None)
      val f = for {
        _ <- dao.createAdvert(advert)
        r <- dao.getAdverts("title")
      } yield r
      f.futureValue must have length 1
    }

    "get advert by id" in {
      val advert = CarAdvert(None, "title", Fuel.Gasoline, 1000, `new` = false, None, None)
      val f = for {
        a <- dao.createAdvert(advert)
        r <- dao.getAdvert(a.id.get)
      } yield r
      f.futureValue.value.title mustBe advert.title
    }

    "delete advert" in {
      val advert = CarAdvert(None, "title", Fuel.Gasoline, 1000, `new` = false, None, None)
      val f = for {
        a <- dao.createAdvert(advert)
        _ <- dao.deleteAdvert(a.id.get)
        r <- dao.getAdverts("title")
      } yield r
      f.futureValue mustBe empty
    }

    "modify advert" in {
      val oldAdvert = CarAdvert(None, "oldtitle", Fuel.Gasoline, 1000, `new` = false, None, None)
      val newAdvert = oldAdvert.copy(title = "newtitle")
      val f = for {
        a <- dao.createAdvert(oldAdvert)
        _ <- dao.modifyAdvert(a.id.get, newAdvert)
        r <- dao.getAdvert(a.id.get)
      } yield r
      f.futureValue.value.title mustBe newAdvert.title
    }

    "sort by price" in {
      val advert1 = CarAdvert(None, "BMW", Fuel.Gasoline, 1000, `new` = false, None, None)
      val advert2 = CarAdvert(None, "Skoda", Fuel.Gasoline, 2000, `new` = false, None, None)

      val f1 = dao.createAdvert(advert1)
      val f2 = dao.createAdvert(advert2)

      val f = for {
        _ <- f1
        _ <- f2
        x <- dao.getAdverts("price")
      } yield x

      implicit val sortable = new Sortable[Seq[CarAdvert]] {
        override def isSorted(seq: Seq[CarAdvert]): Boolean = (seq, seq.tail).zipped.forall(_.price <= _.price)
      }

      f.futureValue mustBe sorted
    }

    "sort by mileage" in {
      val advert1 = CarAdvert(None, "BMW", Fuel.Gasoline, 1000, `new` = false, Some(10000), None)
      val advert2 = CarAdvert(None, "Skoda", Fuel.Gasoline, 2000, `new` = false, Some(5000), None)

      val f1 = dao.createAdvert(advert1)
      val f2 = dao.createAdvert(advert2)

      val f = for {
        _ <- f1
        _ <- f2
        x <- dao.getAdverts("mileage")
      } yield x

      implicit val sortable = new Sortable[Seq[CarAdvert]] {
        override def isSorted(seq: Seq[CarAdvert]): Boolean = (seq, seq.tail).zipped.forall(_.mileage.value <= _.mileage.value)
      }

      f.futureValue mustBe sorted
    }

    "sort by first registration" in {
      val advert1 = CarAdvert(None, "BMW", Fuel.Gasoline, 1000, `new` = false, None, Some(LocalDate.of(2015, 1, 1)))
      val advert2 = CarAdvert(None, "Skoda", Fuel.Gasoline, 2000, `new` = false, None, Some(LocalDate.of(2015, 2, 2)))

      val f1 = dao.createAdvert(advert1)
      val f2 = dao.createAdvert(advert2)

      val f = for {
        _ <- f1
        _ <- f2
        x <- dao.getAdverts("firstRegistration")
      } yield x

      implicit val sortable = new Sortable[Seq[CarAdvert]] {
        override def isSorted(seq: Seq[CarAdvert]): Boolean = (seq, seq.tail).zipped
          .forall((a1, a2) => a1.firstRegistration.value.compareTo(a2.firstRegistration.value) <= 0)
      }

      f.futureValue mustBe sorted
    }

  }
}
