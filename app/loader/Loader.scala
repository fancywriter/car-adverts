package loader

import actors.EventsHub
import akka.actor.{ActorRef, ActorSystem}
import controllers.{AssetsComponents, CarAdvertsController}
import dao.CarAdvertsDao
import play.api.db.DBApi
import play.api.db.evolutions.EvolutionsComponents
import play.api.db.slick.evolutions.SlickDBApi
import play.api.db.slick.{DbName, SlickComponents}
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext}
import play.filters.HttpFiltersComponents
import router.Routes

class Loader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application = new Components(context).application
}

class Components(context: ApplicationLoader.Context)
  extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with AssetsComponents
    with EvolutionsComponents
    with SlickComponents {

  lazy val dbApi: DBApi = SlickDBApi(slickApi)

  applicationEvolutions

  lazy val carAdvertsDao = new CarAdvertsDao(slickApi.dbConfig(DbName("default")))

  implicit def system: ActorSystem = actorSystem

  lazy val eventsHub: ActorRef = system.actorOf(EventsHub.props)

  lazy val carAdvertsController = new CarAdvertsController(carAdvertsDao, eventsHub, controllerComponents)

  override def router: Router = new Routes(httpErrorHandler, carAdvertsController, assets)
}
