package com.apetheriotis.client.routes

import spray.routing.HttpService
import spray.httpx.SprayJsonSupport
import spray.json._
import com.apetheriotis.client.dto.LatestNumberStatsRs
import com.apetheriotis.client.services.StatsService

/**
 * Defines API resources for admin operations.
 * No security has been implemented.
 */
trait AdminService extends HttpService with SprayJsonSupport with DefaultJsonProtocol {
  implicit val adminTicketRs = jsonFormat3(LatestNumberStatsRs)

  val statsService = new StatsService

  val adminApiRoutes =
    path("api" / "v1" / "json" / "admin" / "stats") {
      get {
        complete {
          statsService.getLatestDrawStats()
        }
      }
    } ~
      path("api" / "v1" / "json" / "admin" / "rsStats") {
        get {
          complete {
            statsService.getRealTimeStats()
          }
        }
      }

}




