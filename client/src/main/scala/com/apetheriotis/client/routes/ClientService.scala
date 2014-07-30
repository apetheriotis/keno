package com.apetheriotis.client.routes

import spray.routing.HttpService
import spray.httpx.SprayJsonSupport
import spray.json._
import com.apetheriotis.client.dto.{CheckTicketRs, LatestDrawResultRs, SubmitTicketRs, SubmitTicketRq}
import com.apetheriotis.client.services.{TicketService, ResultsService}

/**
 * Defines API resources for client operations.
 * No security has been implemented.
 */
trait ClientService extends HttpService with SprayJsonSupport with DefaultJsonProtocol {
  implicit val submitTicketRq = jsonFormat1(SubmitTicketRq)
  implicit val submitTicketRs = jsonFormat4(SubmitTicketRs)
  implicit val checkTicketRs = jsonFormat3(CheckTicketRs)
  implicit val resultsRs = jsonFormat2(LatestDrawResultRs)

  val resultsService = new ResultsService
  val ticketService = new TicketService

  val clientApiRoutes =
    path("api" / "v1" / "json" / "ticket") {
      entity(as[SubmitTicketRq]) {
        ticket =>
          complete {
            ticketService.submitTicket(1, ticket.numbers)
          }
      }
    } ~
      path("api" / "v1" / "json" / "ticket" / Segment) {
        ticketId =>
          get {
            complete {
              ticketService.checkTicketRs(ticketId)
            }
          }
      } ~
      path("api" / "v1" / "json" / "results") {
        get {
          complete {
            resultsService.setupLatestResults()
          }
        }
      }
}