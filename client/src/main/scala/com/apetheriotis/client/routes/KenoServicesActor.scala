package com.apetheriotis.client.routes

import akka.actor.Actor
import spray.routing.HttpService
import org.json4s.{DefaultFormats, Formats}
import com.apetheriotis.client.routes.AdminService

/**
 * An actor that runs  the routes
 */
class KenoServicesActor extends Actor with HttpService with StaticPagesService with ClientService with AdminService {

  implicit def json4sFormats: Formats = DefaultFormats

  def actorRefFactory = context

  def receive = runRoute(clientApiRoutes ~ staticPagesRoutes ~ adminApiRoutes)

}