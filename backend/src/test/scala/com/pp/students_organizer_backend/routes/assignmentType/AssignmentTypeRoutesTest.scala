package com.pp.students_organizer_backend.routes.assignmentType

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.gateways.assignmentType.AssignmentTypeRoutesGateway
import com.pp.students_organizer_backend.routes.assignmentType.models.response.GetAssignmentTypeResponse
import com.pp.students_organizer_backend.services.AssignmentTypeService
import com.pp.students_organizer_backend.test_utils.RoutesChecker
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.{Decoder, Encoder, Json}
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.jsonEncoder
import org.http4s.implicits.uri
import org.http4s.{Method, Request, Response, Status}
import org.mockito.Mockito.when
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
        .run(Request(method = Method.GET, uri = uri"/assignmentType"))

    RoutesChecker.check(
      expectedStatus = Status.Ok,
      expectedResponse = expectedResponse,
      actualResponse = actualResponse
    )
  }

  private def tested(
      gateway: AssignmentTypeRoutesGateway[IO] =
        mock[AssignmentTypeRoutesGateway[IO]]
  ): AssignmentTypeRoutes[IO] =
    AssignmentTypeRoutes(gateway)
