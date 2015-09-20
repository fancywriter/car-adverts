package db

import java.time.LocalDate
import java.util.UUID

import models.{CarAdvert, Fuel}
import net.java.truecommons.shed.ResourceLoan._
import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CarAdvertSql {

  def database = Database.forConfig("h2mem1")

  class CarAdverts(tag: Tag) extends Table[(String, String, String, Int, Boolean, Option[Int], Option[String])](tag, "CAR_ADVERT") {
    def id = column[String]("id", O.PrimaryKey)

    def title = column[String]("title")

    def fuel = column[String]("fuel")

    def price = column[Int]("price")

    def `new` = column[Boolean]("new")

    def mileage = column[Option[Int]]("mileage")

    def firstRegistration = column[Option[String]]("firstRegistration")

    override def * = (id, title, fuel, price, `new`, mileage, firstRegistration)
  }

  val adverts = TableQuery[CarAdverts]

  loan(database) to { db =>
    db.run(adverts.schema.create)
  }

  def getAdverts(sort: String): Future[Seq[CarAdvert]] = {
    loan(database) to { db =>
      db.run(adverts.sortBy(x => sort match {
        case "title" => x.title.asc
        case "fuel" => x.fuel.asc
        case "price" => x.price.asc
        case "new" => x.`new`.asc
        case "mileage" => x.mileage.asc
        case "firstRegistration" => x.firstRegistration.asc
        case _ => x.id.asc
      }).result).map(_.map(fromTableRow))
    }
  }

  def getAdvert(id: UUID): Future[Option[CarAdvert]] = {
    loan(database) to { db =>
      db.run(adverts.filter(_.id === id.toString).result.headOption).map(_.map(fromTableRow))
    }
  }

  def modifyAdvert(id: UUID, a: CarAdvert) = {
    val a1 = a.copy(id = Some(id))
    loan(database) to { db =>
      db.run(adverts.filter(_.id === id.toString).update(toTableRow(a1)))
    }
  }

  def createAdvert(a: CarAdvert) = {
    val id = UUID.randomUUID()
    val a1 = a.copy(id = Some(id))
    loan(database) to { db =>
      db.run(adverts += toTableRow(a1))
    }
    a1
  }

  def deleteAdvert(id: UUID) = {
    loan(database) to { db =>
      db.run(adverts.filter(_.id === id.toString).delete)
    }
  }

  private def fromTableRow(row: (String, String, String, Int, Boolean, Option[Int], Option[String])) = {
    val (id, title, fuel, price, isNew, mileage, firstRegistration) = row
    CarAdvert(Some(UUID.fromString(id)), title, Fuel.withName(fuel), price, isNew, mileage, firstRegistration.map(LocalDate.parse))
  }

  private def toTableRow(a: CarAdvert) = {
    (a.id.get.toString, a.title, a.fuel.toString, a.price, a.`new`, a.mileage, a.firstRegistration.map(_.toString))
  }


}
