package com.gatling.tests.api

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._
class APITest1  extends Simulation{

 // protocol
 val httpProtocol =http.baseUrl(url="https://dummyjson.com/products")

 // scenario
 val scn= scenario(scenarioName = "Get API Request Demo")
   .exec(
    http(requestName = "Get single product")
      .get("/25")
      .check(status.is(expected = 200))
      // jsonPath(path = "$.x.category").is(expected = "groceries"))
   )

   .pause(duration = 1)
 //  setup
 setUp(
  scn.inject(rampUsers(users = 10).during(5))
    .protocols(httpProtocol)
 )
}
