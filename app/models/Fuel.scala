package models

import scala.collection.breakOut

sealed trait Fuel
object Fuel {
  case object Gasoline extends Fuel
  case object Diesel extends Fuel

  private val Values: Map[String, Fuel] = Seq(Gasoline, Diesel).map(fuel => fuel.toString.toLowerCase -> fuel)(breakOut)

  def apply(s: String): Fuel = Values.getOrElse(s.toLowerCase, Fuel.Gasoline)
  def get(s: String): Option[Fuel] = Values.get(s)
}
