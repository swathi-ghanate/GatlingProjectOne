package com.gatling.tests.api

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.util.UUID

import scala.language.postfixOps

class MozHealth extends Simulation{
  val tenantId= "mz"
  // protocol
  val httpProtocol = http
    .baseUrl(url = "https://moz-health-qa.digit.org")
  // scenario
  val scn = scenario(scenarioName = "Login")
    .exec(
      http(requestName = "Get the Token")
        .post("/user/oauth/token")
        .headers(Map("Authorization" -> "Basic ZWdvdi11c2VyLWNsaWVudDo=",
          "Content-Type" -> "application/x-www-form-urlencoded", "accept" -> "application/json, text/plain, */*")
        )
        .formParam("grant_type","password")
        .formParam("username","GOD1")
        .formParam("password","eGov@4321")
        .formParam("scope", "read")
        .formParam("tenantId", tenantId)
        .formParam("userType", "EMPLOYEE")
        .check(status.is(expected = 200))
        .check(bodyString.saveAs("Auth_Response"))
        .check(jsonPath("$.access_token").saveAs("authToken"))
    )
    .exec(session => {
      val newSession =  session.set("uuid1", UUID.randomUUID().toString())
        .set("uuid2", UUID.randomUUID().toString())
        .set("uuid3", UUID.randomUUID().toString())
        .set("uuid4", UUID.randomUUID().toString())
      System.out.println(newSession);
      newSession;
    })
    .exec(http(requestName = "Create Household")
      .post("/household/v1/bulk/_create")
      .headers(Map("Content-Type" -> "application/json"))
      .body(StringBody(
        """
              {
            "RequestInfo": {
                "authToken": "${authToken}"
            },
            "Households":[
             {
                "tenantId": "mz",
                "clientReferenceId": "${uuid1}",
                "memberCount": 5,
                "address": {

                    "tenantId": "mz",
                    "doorNo": "123",
                    "latitude": 12.34,
                    "longitude": 123.45,
                    "locationAccuracy": 123.76,
                    "type": "1",
                    "addressLine1": "address",
                    "addressLine2": "Road Number 12",
                    "landmark": "Near Niagara Lake",
                    "city": "Bangalore",
                    "pincode": "1234567",
                    "buildingName": "Alpine Apartments",
                    "street": "test_da0b8ebec5ef",
                    "locality": {
                        "code": "locality code",
                        "name": "locality name"
                    }
                },
                "additionalFields": {
                    "schema": "test_e37466be924cjhghjg",
                    "version": 8,
                    "fields": [
                        {
                            "key": "test_12bc5f24692f",
                            "value": "test_bf376bce4c01"
                        }
                    ]
                },
                "rowVersion": 0
                 }
                 ]
        }

       """))
      .check(status.is(202))
      .check(bodyString.saveAs("responseBody"))
    )
    .exec(http(requestName = "Create Individuals")
      .post("/individual/v1/bulk/_create")
      .headers(Map("Content-Type" -> "application/json"))
      .body(StringBody(
        """
          {
        "RequestInfo": {
            "authToken": "${authToken}"
        },
       "Individuals": [ {
    "tenantId": "mz",
    "clientReferenceId": "${uuid2}",
    "name": {
        "givenName": "Souravtc5m3sbrn6",
        "familyName": "Gangulyp9uzsbmck9",
        "otherNames": "Dadavu8wv8vepr"
    },
    "dateOfBirth": "25/12/1991",
    "gender": "FEMALE",
    "bloodGroup": "A-",
    "mobileNumber": "9876543210",
    "altContactNumber": "8765432109",
    "email": "souravganguly_dada@gmail.com",
    "address": [
        {

           "tenantId": "mz",
            "doorNo": "123",
            "latitude": 40.82,
            "longitude": 35.06,
            "locationAccuracy": 14.27,
            "type": "PERMANENT",
            "addressLine1": "Apartment",
            "addressLine2": "2nd Floor",
            "landmark": "Near Juhu Beach",
            "city": "Mumbai",
            "pincode": "123456",
            "buildingName": "Apartment",
            "street": "Necklace Road",
            "locality": {
                "code": "test_9b31746b933d",
                "name": "test_58630a388978",
                "label": "test_7b7f928dcab8",
                "latitude": "test_15d4a90d15a6",
                "longitude": "test_e8854daed039",
                "children": []
            }
        }
    ],
    "fatherName": "Ganguly Sr",
    "husbandName": null,
    "identifiers": [
        {
            "identifierType": "PASSPORT",
            "identifierId": "931419274506",
            "clientReferenceId": "${uuid3}"
        }
    ],
    "skills": [
        {
            "type": "Science",
            "level": "Learner",
            "experience": "3 Years",
            "clientReferenceId": "${uuid3}"
        }
    ],
    "photo": "test_35bd4e8545d8",
    "additionalFields": {
        "schema": "test_f76b4cca6bff",
        "version": 23,
        "fields": [
            {
                "key": "test_80f7fb8d8a45",
                "value": "test_38bfd0dc223b"
            }
        ]
    },
    "rowVersion": 1
}
             ]
    }
    """))
      .check(status.is(202))
      .check(bodyString.saveAs("responseBody"))
    )
    .exec(http(requestName = "Create HouseholdMembers")
      .post("/household/member/v1/bulk/_create")
      .headers(Map("Content-Type" -> "application/json"))
      .body(StringBody(
        """
          {
        "RequestInfo": {
            "authToken": "${authToken}"
        },
      "HouseholdMembers": [
    {
        "householdClientReferenceId": "${uuid1}",
        "individualClientReferenceId": "${uuid2}",
        "isHeadOfHousehold": true,
        "tenantId": "mz",
        "rowVersion": 1
       }
]
}
 """))
      .check(status.is(202))
      .check(bodyString.saveAs("responseBody"))
    )
    .exec(http(requestName = "Create ProjectBeneficiaries")
      .post("/project/beneficiary/v1/bulk/_create")
      .headers(Map("Content-Type" -> "application/json"))
      .body(StringBody(
        """
          {
        "RequestInfo": {
            "authToken": "${authToken}"
        },
      "ProjectBeneficiaries": [
    {
        "clientReferenceId": "${uuid4}",
        "tenantId": "mz",
        "projectId": "d40387f6-9110-42c6-833e-f618e5fcfc98",
        "beneficiaryClientReferenceId": "f8e5cd69-3b73-43b5-b675-3a0e40f4f3d1",
        "dateOfRegistration": 1689233700911,
        "additionalFields": {
            "schema": "registration",
            "version": 1,
            "fields": [
                {
                    "key": "key1",
                    "value": "value1"
                }
            ]
        }
    }
]
}
   """))
      .check(status.is(202))
      .check(bodyString.saveAs("responseBody"))
    )
    .exec(http(requestName = "Delivery the bednets")
      .post("/project/task/v1/bulk/_create")
      .headers(Map("Content-Type" -> "application/json"))
      .body(StringBody(
        """
          {
        "RequestInfo": {
            "authToken": "${authToken}"
            },
          "Tasks": [
    {
    "tenantId": "mz",
    "projectId": "d40387f6-9110-42c6-833e-f618e5fcfc98",
    "projectBeneficiaryClientReferenceId": "8e888b14-7002-4e9c-ae88-cbd71d9c402f",
    "clientReferenceId": "${uuid1}",
    "resources": [
        {
            "tenantId": "mz",
            "clientReferenceId": "${uuid2}",
            "taskClientReferenceId": "${uuid3}",
            "productVariantId": "PVAR-2023-07-25-000003",
            "quantity": 2,
            "isDelivered": true,
            "deliveryComment": "Service delivery is completed"
            }
    ],
    "plannedStartDate": 1675582245,
    "plannedEndDate": 1707723045,
    "actualStartDate": 1707550245,
    "actualEndDate": 1707590245,
    "createdBy": "56b5c31b-b843-426c-b8d5-64a4c80286aa",
    "createdDate": 1707550245,
    "address": {
        "tenantId": "mz",
        "doorNo": "test_4b2c5ad43e11",
        "latitude": 86.78,
        "longitude": 77.12,
        "locationAccuracy": 65.67,
        "type": "PERMANENT",
        "addressLine1": "test_2512bdc41cb2",
        "addressLine2": "test_afa1eadcb4f0",
        "landmark": "test_d7ee474cb6da",
        "city": "test_932d23b00265",
        "pincode": "test_637fa8890171",
        "buildingName": "test_90f36fdaf51d",
        "street": "test_105ae34b63c6",
        "locality": {
            "code": "test_218f32cdca88",
            "name": "test_66187cb44cb7",
            "label": "test_fd101b472626",
            "latitude": "test_1b1c8cbc83c3",
            "longitude": "test_f42b04a12d34",
            "materializedPath": "test_e5388dd34db2"
        }
    },
    "additionalFields": {
        "schema": "test_5266efb5bcd9",
        "version": 24,
        "fields": [
            {
                "key": "test_b9aa6f50056e",
                "value": "test_dcfafb1be02f"
            }
        ]
    },
    "rowVersion": 1,
    "status": ""
       }
]
}
"""))
      .check(status.is(202))
      .check(bodyString.saveAs("responseBody"))
    )

  //  setup
  setUp(
    scn.inject(atOnceUsers(users = 10),rampUsers(20).during(10)).
      protocols(httpProtocol)
  )
}

