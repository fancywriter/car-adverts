package dao

import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

import models.{Fuel, CarAdvert}
import models.Fuel.Fuel
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

class CarAdvertDao @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  implicit val localDateColumn = MappedColumnType.base[LocalDate, String](_.toString, LocalDate.parse)
  implicit val fuelColumn = MappedColumnType.base[Fuel, String](_.toString, Fuel.withName)

  class CarAdverts(tag: Tag) extends Table[CarAdvert](tag, "CAR_ADVERT") {
    def id = column[Option[UUID]]("ID", O.PrimaryKey)

    def title = column[String]("TITLE")

    def fuel = column[Fuel]("FUEL")

    def price = column[Int]("PRICE")

    def `new` = column[Boolean]("NEW")

    def mileage = column[Option[Int]]("MILEAGE")

    def firstRegistration = column[Option[LocalDate]]("FIRST_REGISTRATION")

    override def * = (id, title, fuel, price, `new`, mileage, firstRegistration) <>(CarAdvert.tupled, CarAdvert.unapply)
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
    db.run(adverts += a1).map(_ => a1)
  }

  def deleteAdvert(id: UUID): Future[Int] = db.run(adverts.filter(_.id === id).delete)

}
