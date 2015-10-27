package models

import java.time.LocalDate
import java.util.UUID

import models.Fuel.Fuel

object Fuel extends Enumeration {
  type Fuel = Value
  val Gasoline, Diesel = Value
}

case class CarAdvert(id: Option[UUID], title: String, fuel: Fuel, price: Int, `new`: Boolean, mileage: Option[Int],
                     firstRegistration: Option[LocalDate])

