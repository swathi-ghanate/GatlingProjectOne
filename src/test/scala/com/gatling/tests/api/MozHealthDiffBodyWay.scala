package com.gatling.tests.api

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.util.UUID

class MozHealthDiffBodyWay extends Simulation {
  val tenantId = "mz"

  // protocol
  val httpProtocol = http
    .baseUrl("https://moz-health-qa.digit.org")

  // scenario
  val scn = scenario("Login")
    .exec(
      http(requestName = "Get the Token")
        .post("/user/oauth/token")
        .headers(Map("Authorization" -> "Basic ZWdvdi11c2VyLWNsaWVudDo=", "Content-Type" -> "application/x-www-form-urlencoded", "accept" -> "application/json, text/plain, */*"))
        .formParam("grant_type", "password")
        .formParam("username", "GOD1")
        .formParam("password", "eGov@4321")
        .formParam("scope", "read")
        .formParam("tenantId", tenantId)
        .formParam("userType", "EMPLOYEE")
        .check(status.is(200))
        .check(bodyString.saveAs("Auth_Response"))
        .check(jsonPath("$.access_token").saveAs("authToken"))
    )
    .exec(session => {
      val newSession = session
        .set("uuid1", UUID.randomUUID().toString())
        .set("uuid2", UUID.randomUUID().toString())
        .set("uuid3", UUID.randomUUID().toString())
        .set("uuid4", UUID.randomUUID().toString())
      System.out.println(newSession)
      newSession
    })
    .exec(
      http(requestName = "Create Household")
        .post("/household/v1/bulk/_create")
        .headers(Map("Content-Type" -> "application/json"))
        .body(RawFileBody("./src/test/resources/Body/Households.json")).asJson
        .check(status.is(202))
        .check(bodyString.saveAs("responseBody"))
    )
    .pause(5)
    .exec(http(requestName = "Create Individuals")
      .post("/individual/v1/bulk/_create")
      .headers(Map("Content-Type" -> "application/json"))
      .body(RawFileBody("./src/test/resources/Body/Individuals.json")).asJson
      .check(status.is(202))
      .check(bodyString.saveAs("responseBody"))
    )
    .pause(7)

    .exec(http(requestName = "Create HouseholdMembers")
      .post("/household/member/v1/bulk/_create")
      .headers(Map("Content-Type" -> "application/json"))
      .body(RawFileBody("./src/test/resources/Body/HouseholdMemebers.json")).asJson
      .check(status.is(202))
      .check(bodyString.saveAs("responseBody"))
    )
    .pause(7)
    .exec(http(requestName = "Create ProjectBeneficiaries")
      .post("/project/beneficiary/v1/bulk/_create")
      .headers(Map("Content-Type" -> "application/json"))
      .body(RawFileBody("./src/test/resources/Body/ProjectBeneficiary.json")).asJson
      .check(status.is(202))
      .check(bodyString.saveAs("responseBody"))
    )
    .pause(7)
    .exec(http(requestName = "Delivery the bednets")
      .post("/project/task/v1/bulk/_create")
      .headers(Map("Content-Type" -> "application/json"))
      .body(RawFileBody("./src/test/resources/Body/ProjectTask.json")).asJson
      .check(status.is(202))
      .check(bodyString.saveAs("responseBody"))
    )
  setUp(
    scn.inject(atOnceUsers(users = 1)).
      protocols(httpProtocol)
  )
}