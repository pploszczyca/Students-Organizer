package com.pp.students_organizer_backend.routes.assignmentType

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.domain.errors.ValidationException
import com.pp.students_organizer_backend.gateways.assignmentType.AssignmentTypeRoutesGateway
import com.pp.students_organizer_backend.routes.AssignmentTypeRoutes
import com.pp.students_organizer_backend.routes_models.assignmentType.request.InsertAssignmentTypeRequest
import com.pp.students_organizer_backend.routes_models.assignmentType.response.GetAssignmentTypeResponse
import com.pp.students_organizer_backend.services.AssignmentTypeService
import com.pp.students_organizer_backend.test_utils.RoutesChecker
import fs2.Stream
import io.circe.generic.auto.*
import io.circe.literal.json
import io.circe.syntax.*
import io.circe.{Decoder, Encoder, Json}
import org.http4s.Method.{DELETE, GET, POST}
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.jsonEncoder
import org.http4s.client.dsl.io.*
import org.http4s.implicits.uri
import org.http4s.{EmptyBody, Method, Request, Response, Status}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock

import java.util.UUID

class AssignmentTypeRoutesTest extends AnyFlatSpec:
  private val gateway = mock[AssignmentTypeRoutesGateway[IO]]

  "GET -> /assignmentType" should "return response" in {
    val assignmentTypeResponse = GetAssignmentTypeResponse(
      id = UUID.fromString("3efe9e6d-4163-40e5-8ea0-aebe46b502c4"),
      name = "name"
    )
    val expectedResponse =
      json"""[
         {
          "id": "3efe9e6d-4163-40e5-8ea0-aebe46b502c4",
          "name": "name"
         }
      ]"""

    when(gateway.getAll) thenReturn IO(List(assignmentTypeResponse))

    val actualResponse =
      tested(gateway = gateway)().orNotFound
        .run(Request(method = GET, uri = uri"/assignmentType"))

    RoutesChecker.check(
      expectedStatus = Status.Ok,
      expectedResponse = expectedResponse,
      actualResponse = actualResponse
    )
  }

  "POST -> /assignmentType" should "insert new assignment type" in {
    val jsonRequest =
      json"""{
         "name": "assignmentType name"
          }"""
    val request = InsertAssignmentTypeRequest(
      name = "assignmentType name"
    )

    when(gateway.insert(any())) thenReturn IO.unit

    val actualResponse = tested(gateway = gateway)().orNotFound
      .run(POST(jsonRequest, uri"/assignmentType"))
      .unsafeRunSync()

    verify(gateway).insert(request)
    RoutesChecker.checkStatus(
      response = actualResponse,
      expectedStatus = Status.Created
    )
  }

  "POST -> /assignmentType" should "return error WHEN exception occurs" in {
    val jsonRequest =
      json"""{
         "name": "assignmentType name"
          }"""
    val request = InsertAssignmentTypeRequest(
      name = "assignmentType name"
    )
    val errorMessage = "error message"
    val exception = ValidationException(errorMessage)
    val expectedResponse =
      json"""
            "error message"
          """

    when(gateway.insert(any())) thenAnswer (* => throw exception)

    val actualResponse = tested(gateway = gateway)().orNotFound
      .run(POST(jsonRequest, uri"/assignmentType"))

    verify(gateway).insert(request)
    RoutesChecker.check(
      expectedStatus = Status.BadRequest,
      expectedResponse = expectedResponse,
      actualResponse = actualResponse
    )
  }

  "DELETE -> /assignmentType" should "delete assignment type" in {
    val assignmentTypeId =
      UUID.fromString("3efe9e6d-4163-40e5-8ea0-aebe46b502c4")

    when(gateway.remove(any())) thenReturn IO.unit

    val actualResponse = tested(gateway = gateway)().orNotFound
      .run(DELETE(uri"/assignmentType/3efe9e6d-4163-40e5-8ea0-aebe46b502c4"))
      .unsafeRunSync()

    verify(gateway).remove(assignmentTypeId)
    RoutesChecker.checkStatus(
      response = actualResponse,
      expectedStatus = Status.NoContent
    )
  }

  private def tested(
      gateway: AssignmentTypeRoutesGateway[IO] =
        mock[AssignmentTypeRoutesGateway[IO]]
  ): AssignmentTypeRoutes[IO] =
    AssignmentTypeRoutes(gateway)
