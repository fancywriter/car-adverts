package utils

import play.api.Configuration

import scala.util.Random

object TestConfig {

  def apply() = Configuration(
    "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
    "slick.dbs.default.db.driver" -> "org.h2.Driver",
    "slick.dbs.default.db.url" -> s"jdbc:h2:mem:play-test-${Random.nextInt()};DB_CLOSE_ON_EXIT=FALSE",
    "slick.dbs.default.db.connectionTimeout" -> 10000
  )

}
