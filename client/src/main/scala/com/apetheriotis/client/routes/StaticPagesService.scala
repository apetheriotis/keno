package com.apetheriotis.client.routes

import spray.routing.HttpService


/**
 * Routes to serve static pages from resources folder
 */
trait StaticPagesService extends HttpService {

  val staticPagesRoutes =
      pathPrefix("static" / Segment) {
        version =>
          get {
           getFromResource("static/" + version)
          }
      }
}