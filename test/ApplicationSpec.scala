import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "show empty list" in new WithApplication {
      val adverts = route(FakeRequest(GET, "/adverts")).get
      status(adverts) must equalTo(OK)
      contentType(adverts) must beSome.which(_ == "application/json")
      contentAsString(adverts) must equalTo("[]")
    }

  }
}
