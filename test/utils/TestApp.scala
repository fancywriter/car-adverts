package utils

import play.api.test.FakeApplication

import scala.util.Random

class TestApp extends FakeApplication(additionalConfiguration = Map(
  "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
  "slick.dbs.default.db.driver" -> "org.h2.Driver",
  "slick.dbs.default.db.url" -> s"jdbc:h2:mem:play-test-${Random.nextInt()};DB_CLOSE_ON_EXIT=FALSE"
))
