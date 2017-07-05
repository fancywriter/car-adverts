package actors

import akka.actor._
import play.api.libs.json.JsValue

class EventsHub extends Actor {
  private var listeners = Set.empty[ActorRef]

  import EventsHub._

  override def receive: Receive = {
    case Add(ref) => listeners += ref
    case Remove(ref) => listeners -= ref
    case Trigger(v) => listeners.foreach(_ ! v)
  }
}

object EventsHub {
  def props = Props(new EventsHub)

  case class Add(ref: ActorRef)

  case class Remove(ref: ActorRef)

  case class Trigger(v: JsValue)

}
