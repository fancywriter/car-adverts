package dao

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import play.api.test.{PlaySpecification, WithApplication}
import utils.TestApp

@RunWith(classOf[JUnitRunner])
class CarAdvertDaoSpec extends PlaySpecification {

  "CarAdvertDao" should {
    val app = new TestApp
    val dao = app.injector.instanceOf[CarAdvertDao]

    "return empty list on empty database" in new WithApplication(app) {
      val adverts = await(dao.getAdverts("title"))
      adverts must beEmpty
    }
  }

}
