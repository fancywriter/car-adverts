package utils

import org.scalatest.{TestData, Inside}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.test.FakeApplication

import scala.util.Random

abstract class BaseSpec extends PlaySpec with ScalaFutures with OneAppPerTest {

  override def newAppForTest(td: TestData) = new FakeApplication(additionalConfiguration = Map(
    "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
    "slick.dbs.default.db.driver" -> "org.h2.Driver",
    "slick.dbs.default.db.url" -> s"jdbc:h2:mem:play-test-${Random.nextInt()};DB_CLOSE_ON_EXIT=FALSE"
  ))

}
