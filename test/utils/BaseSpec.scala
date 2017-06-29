package utils

import org.scalatest.TestData
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Configuration}

import scala.util.Random

abstract class BaseSpec extends PlaySpec with ScalaFutures with GuiceOneAppPerTest {

  override def newAppForTest(td: TestData): Application = GuiceApplicationBuilder(
    configuration = Configuration(
      "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
      "slick.dbs.default.db.driver" -> "org.h2.Driver",
      "slick.dbs.default.db.url" -> s"jdbc:h2:mem:play-test-${Random.nextInt()};DB_CLOSE_ON_EXIT=FALSE",
      "slick.dbs.default.db.connectionTimeout" -> 10000
    )
  ).build()

}
