package actors

import akka.actor._
import play.api.libs.json.JsValue

class Listener(eventsHub: ActorRef, out: ActorRef) extends Actor {

  override def preStart(): Unit = {
    eventsHub ! EventsHub.Add(out)
  }

  override def postStop(): Unit = {
    eventsHub ! EventsHub.Remove(out)
  }

  override def receive: Receive = {
    case _: JsValue =>
  }
}

object Listener {
  def props(eventsHub: ActorRef)(out: ActorRef) = Props(new Listener(eventsHub, out))
}
