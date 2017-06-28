package models

import java.time.LocalDate
import java.util.UUID

case class CarAdvert(
  id: Option[UUID],
  title: String,
  fuel: Fuel,
  price: Int,
  `new`: Boolean,
  mileage: Option[Int],
  firstRegistration: Option[LocalDate]
)
