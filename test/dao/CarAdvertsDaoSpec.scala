package dao

import java.time.LocalDate

import com.typesafe.config.ConfigFactory
import models.{CarAdvert, Fuel}
import org.scalatest.MustMatchers._
import org.scalatest.OptionValues._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.enablers.Sortable
import org.scalatest.{Outcome, fixture}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global

class CarAdvertsDaoSpec extends fixture.FlatSpec with ScalaFutures with IntegrationPatience {

  "CarAdvertDao" should "return empty list" in { dao =>
    val f = dao.getAdverts("title")
    f.futureValue mustBe empty
  }

  it should "create new advert" in { dao =>
    val advert = CarAdvert(None, "title", Fuel.Gasoline, 1000, `new` = false, None, None)
    val f = for {
      _ <- dao.createAdvert(advert)
      r <- dao.getAdverts("title")
    } yield r
    f.futureValue must have length 1
  }

  it should "get advert by id" in { dao =>
    val advert = CarAdvert(None, "title", Fuel.Gasoline, 1000, `new` = false, None, None)
    val f = for {
      a <- dao.createAdvert(advert)
      r <- dao.getAdvert(a.id.get)
    } yield r
    f.futureValue.value.title mustBe advert.title
  }

  it should "delete advert" in { dao =>
    val advert = CarAdvert(None, "title", Fuel.Gasoline, 1000, `new` = false, None, None)
    val f = for {
      a <- dao.createAdvert(advert)
      _ <- dao.deleteAdvert(a.id.get)
      r <- dao.getAdverts("title")
    } yield r
    f.futureValue mustBe empty
  }

  it should "modify advert" in { dao =>
    val oldAdvert = CarAdvert(None, "oldtitle", Fuel.Gasoline, 1000, `new` = false, None, None)
    val newAdvert = oldAdvert.copy(title = "newtitle")
    val f = for {
      a <- dao.createAdvert(oldAdvert)
      _ <- dao.modifyAdvert(a.id.get, newAdvert)
      r <- dao.getAdvert(a.id.get)
    } yield r
    f.futureValue.value.title mustBe newAdvert.title
  }

  it should "sort by price" in { dao =>
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

  it should "sort by mileage" in { dao =>
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

  it should "sort by first registration" in { dao =>
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

  override type FixtureParam = CarAdvertsDao

  override protected def withFixture(test: OneArgTest): Outcome = {
    val config = DatabaseConfig.forConfig[JdbcProfile]("slick.dbs.default", ConfigFactory.load("test"))
    val dao: CarAdvertsDao = new CarAdvertsDao(config)

    import config._
    import profile.api._

    try {
      whenReady(db.run(dao.CarAdverts.schema.create)) { _ =>
        withFixture(test.toNoArgTest(dao))
      }
    } finally {
      db.run(dao.CarAdverts.schema.drop)
    }
  }
}
