package dao

import java.time.LocalDate
import java.util.UUID

import models._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent._

class CarAdvertsDao(val config: DatabaseConfig[JdbcProfile]) {

  import config._
  import profile.api._

  implicit val localDateColumn: BaseColumnType[LocalDate] = MappedColumnType.base[LocalDate, String](_.toString, LocalDate.parse)
  implicit val fuelColumn: BaseColumnType[Fuel] = MappedColumnType.base[Fuel, String](_.toString.toLowerCase, Fuel.apply)

  class CarAdverts(tag: Tag) extends Table[CarAdvert](tag, "CAR_ADVERT") {
    def id: Rep[Option[UUID]] = column[Option[UUID]]("ID", O.PrimaryKey)

    def title: Rep[String] = column[String]("TITLE")

    def fuel: Rep[Fuel] = column[Fuel]("FUEL")

    def price: Rep[Int] = column[Int]("PRICE")

    def `new`: Rep[Boolean] = column[Boolean]("NEW")

    def mileage: Rep[Option[Int]] = column[Option[Int]]("MILEAGE")

    def firstRegistration: Rep[Option[LocalDate]] = column[Option[LocalDate]]("FIRST_REGISTRATION")

    override def * : ProvenShape[CarAdvert] = (id, title, fuel, price, `new`, mileage, firstRegistration) <> (CarAdvert.tupled, CarAdvert.unapply)
  }

  private val adverts = TableQuery[CarAdverts]

  def getAdverts(sort: String): Future[Seq[CarAdvert]] =
    db.run(adverts.sortBy(x => sort match {
      case "title" => x.title.asc
      case "price" => x.price.asc
      case "mileage" => x.mileage.asc
      case "firstRegistration" => x.firstRegistration.asc
      case _ => x.id.asc
    }).result)

  def getAdvert(id: UUID): Future[Option[CarAdvert]] = db.run(adverts.filter(_.id === id).result.headOption)

  def modifyAdvert(id: UUID, a: CarAdvert): Future[Int] = {
    val a1 = a.copy(id = Some(id))
    db.run(adverts.filter(_.id === id).update(a1))
  }

  def createAdvert(a: CarAdvert): Future[CarAdvert] = {
    val a1 = a.copy(id = Some(UUID.randomUUID()))
    db.run(adverts += a1).map(_ => a1)(db.ioExecutionContext)
  }

  def deleteAdvert(id: UUID): Future[Int] = db.run(adverts.filter(_.id === id).delete)

}
