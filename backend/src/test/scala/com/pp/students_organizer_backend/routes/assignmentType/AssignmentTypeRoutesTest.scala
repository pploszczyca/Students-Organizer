package com.pp.students_organizer_backend.routes.assignmentType

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.gateways.assignmentType.AssignmentTypeRoutesGateway
import com.pp.students_organizer_backend.routes.assignmentType.models.request.InsertAssignmentTypeRequest
import com.pp.students_organizer_backend.routes.assignmentType.models.response.GetAssignmentTypeResponse
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

class AssignmentTypeRoutesTest extends AnyFlatSpec:
  private val gateway = mock[AssignmentTypeRoutesGateway[IO]]

  "GET -> /assignmentType" should "return response" in {
    val assignmentTypeResponse = GetAssignmentTypeResponse(
      id = 42,
      name = "name"
    )
    val expectedResponse = List(assignmentTypeResponse).asJson

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

  "DELETE -> /assignmentType" should "delete assignment type" in {
    val assignmentTypeId = 1

    when(gateway.remove(any())) thenReturn IO.unit

    val actualResponse = tested(gateway = gateway)().orNotFound
      .run(DELETE(uri"/assignmentType/1"))
      .unsafeRunSync()

    verify(gateway).remove(assignmentTypeId)
    RoutesChecker.checkStatus(
      response = actualResponse,
      expectedStatus = Status.NoContent,
    )
  }

  private def tested(
      gateway: AssignmentTypeRoutesGateway[IO] =
        mock[AssignmentTypeRoutesGateway[IO]]
  ): AssignmentTypeRoutes[IO] =
    AssignmentTypeRoutes(gateway)
