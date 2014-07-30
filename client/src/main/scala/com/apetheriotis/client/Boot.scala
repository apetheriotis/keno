package com.apetheriotis.client

import akka.actor.{Props, ActorSystem}
import spray.can.Http
import akka.io.IO
import com.apetheriotis.client.routes.KenoServicesActor

object Boot extends App {
  implicit val system = ActorSystem("kenoActorSystem")
  val service = system.actorOf(Props[KenoServicesActor], "apiService")
  IO(Http) ! Http.Bind(service, interface = "0.0.0.0", port = 8083)
}