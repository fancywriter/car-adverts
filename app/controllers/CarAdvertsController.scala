package controllers

import java.util.UUID

import actors.{EventsHub, Listener}
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import dao.CarAdvertsDao
import formats.CarAdvertFormats._
import models.CarAdvert
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent._

class CarAdvertsController
(
  dao: CarAdvertsDao,
  eventsHub: ActorRef,
  components: ControllerComponents
)(implicit
  executionContext: ExecutionContext,
  system: ActorSystem,
  materializer: Materializer
) extends AbstractController(components) {

  import components._

  def getAdverts(sort: String): Action[AnyContent] = actionBuilder.async {
    dao.getAdverts(sort).map(s => Ok(Json.toJson(s)))
  }

  def getAdvert(id: UUID): Action[AnyContent] = actionBuilder.async {
    dao.getAdvert(id).map(_.fold(NotFound("Not Found"))(a => Ok(Json.toJson(a))))
  }

  def createAdvert(): Action[JsValue] = actionBuilder.async(parsers.json) { request =>
    request.body.validate[CarAdvert].fold(badRequest, { a: CarAdvert =>
      dao.createAdvert(a).map { x =>
        val v = Json.toJson(x)
        eventsHub ! EventsHub.Trigger(Json.obj("created" -> v))
        Ok(v)
      }
    })
  }

  def modifyAdvert(id: UUID): Action[JsValue] = actionBuilder.async(parsers.json) { request =>
    val body = request.body
    body.validate[CarAdvert].fold(badRequest, { a: CarAdvert =>
      dao.modifyAdvert(id, a).map { x =>
        eventsHub ! EventsHub.Trigger(Json.obj("updated" -> Json.obj(id.toString -> body)))
        Ok(Json.toJson(x))
      }
    })
  }

  def deleteAdvert(id: UUID): Action[AnyContent] = actionBuilder.async {
    dao.deleteAdvert(id).map { x =>
      eventsHub ! EventsHub.Trigger(Json.obj("deleted" -> id.toString))
      Ok(Json.toJson(x))
    }
  }

  def subscribe: WebSocket = WebSocket.accept[JsValue, JsValue] { _ =>
    ActorFlow.actorRef(Listener.props(eventsHub))
  }

  private[this] def badRequest(errors: Seq[(JsPath, Seq[JsonValidationError])]) =
    Future.successful(BadRequest(JsError.toJson(errors)))

}
