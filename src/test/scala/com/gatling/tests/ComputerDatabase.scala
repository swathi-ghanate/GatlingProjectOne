package com.gatling.tests

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class ComputerDatabase extends Simulation {

	val httpProtocol = http
		.baseUrl("https://computer-database.gatling.io")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-GB,en-US;q=0.9,en;q=0.8")
		.upgradeInsecureRequestsHeader("1")
		.userAgentHeader("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36")




	val scn = scenario("ComputerDatabase")
		.exec(http("request_0")
			.get("/computers")
			)
		.pause(8)
		.exec(http("request_1")
			.get("/computers/new")
		)
		.pause(86)
		.exec(http("request_2")
			.post("/computers")

			.formParam("name", "Mycomputer1")
			.formParam("introduced", "2022-11-29")
			.formParam("discontinued", "2023-11-29")
			.formParam("company", "2"))
		.pause(11)
		.exec(http("request_3")
			.get("/computers?f=Mycomputer1")
		)

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}