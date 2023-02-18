package com.pp.students_organizer_backend.routes

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.errors.ValidationException
import com.pp.students_organizer_backend.gateways.material.MaterialGateway
import com.pp.students_organizer_backend.routes.MaterialRoutes
import com.pp.students_organizer_backend.routes_models.material.request.InsertMaterialRequest
import com.pp.students_organizer_backend.routes_models.material.response.GetMaterialResponse
import com.pp.students_organizer_backend.test_utils.RoutesChecker
import io.circe.literal.json
import org.http4s.Method.{DELETE, GET, POST}
import org.http4s.Status
import org.http4s.circe.jsonEncoder
import org.http4s.client.dsl.io.*
import org.http4s.implicits.uri
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock

import java.util.UUID

class MaterialRoutesTest extends AnyFlatSpec:
  private val gateway = mock[MaterialGateway[IO]]

  "GET -> /material" should "return all materials" in {
    val materialResponse = GetMaterialResponse(
      id = UUID.fromString("3efe9e6d-4163-40e5-8ea0-aebe46b502c4"),
      name = "name",
      url = "www.test.com"
    )
    val expectedResponse =
      json"""[
         {
          "id": "3efe9e6d-4163-40e5-8ea0-aebe46b502c4",
          "name": "name",
          "url":  "www.test.com"
         }
      ]"""

    when(gateway.getAll) thenReturn IO(List(materialResponse))

    val actualResponse = tested(gateway = gateway)().orNotFound
      .run(GET(uri"/material"))

    RoutesChecker.check(
      expectedStatus = Status.Ok,
      expectedResponse = expectedResponse,
      actualResponse = actualResponse
    )
  }

  "POST -> /material" should "insert new material" in {
    val jsonRequest =
      json"""{
            "name": "name",
            "url":  "www.test.com",
            "assignmentId": "817ec3ac-23eb-421f-a898-8debfbc54b46"
          }"""
    val request = InsertMaterialRequest(
      name = "name",
      url = "www.test.com",
      assignmentId = "817ec3ac-23eb-421f-a898-8debfbc54b46",
    )

    when(gateway.insert(any())) thenReturn IO.unit

    val actualResponse = tested(gateway = gateway)().orNotFound
      .run(POST(jsonRequest, uri"/material"))
      .unsafeRunSync()

    verify(gateway).insert(request)
    RoutesChecker.checkStatus(
      response = actualResponse,
      expectedStatus = Status.Created
    )
  }

  "POST -> /material" should "return error WHEN exception occurs" in {
    val jsonRequest =
      json"""{
            "name": "name",
            "url":  "www.test.com",
            "assignmentId": "817ec3ac-23eb-421f-a898-8debfbc54b46"
          }"""
    val request = InsertMaterialRequest(
      name = "name",
      url = "www.test.com",
      assignmentId ="817ec3ac-23eb-421f-a898-8debfbc54b46",
    )

    val errorMessage = "error message"
    val exception = ValidationException(errorMessage)
    val expectedResponse =
      json"""
            "error message"
          """

    when(gateway.insert(any())) thenAnswer (* => throw exception)

    val actualResponse = tested(gateway = gateway)().orNotFound
      .run(POST(jsonRequest, uri"/material"))

    verify(gateway).insert(request)
    RoutesChecker.check(
      expectedStatus = Status.BadRequest,
      expectedResponse = expectedResponse,
      actualResponse = actualResponse
    )
  }

  "DELETE -> /material" should "delete material" in {
    val materialId = UUID.fromString("3efe9e6d-4163-40e5-8ea0-aebe46b502c4")

    when(gateway.remove(any())) thenReturn IO.unit

    val actualResponse = tested(gateway = gateway)().orNotFound
      .run(DELETE(uri"/material/3efe9e6d-4163-40e5-8ea0-aebe46b502c4"))
      .unsafeRunSync()

    verify(gateway).remove(materialId)
    RoutesChecker.checkStatus(
      response = actualResponse,
      expectedStatus = Status.NoContent
    )
  }

  private def tested(
      gateway: MaterialGateway[IO] = mock
  ): MaterialRoutes[IO] =
    MaterialRoutes(gateway)
