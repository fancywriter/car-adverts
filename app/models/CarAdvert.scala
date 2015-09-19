package models

import java.time.LocalDate
import java.util.UUID

import models.Fuel.Fuel

import scala.collection.mutable.ListBuffer

object Fuel extends Enumeration {
  type Fuel = Value
  val Gasoline, Diesel = Value
}

case class CarAdvert(id: Option[UUID], title: String, fuel: Fuel, price: Int, `new`: Boolean, mileage: Option[Int],
                     firstRegistration: Option[LocalDate])

object CarAdvert {

  val adverts = ListBuffer(
    CarAdvert(Some(UUID.randomUUID()), "Audi", Fuel.Gasoline, 10000, `new` = true, None, None),
    CarAdvert(Some(UUID.randomUUID()), "BMW", Fuel.Gasoline, 5000, `new` = true, None, None),
    CarAdvert(Some(UUID.randomUUID()), "Skoda", Fuel.Gasoline, 9000, `new` = true, None, None)
  )

}