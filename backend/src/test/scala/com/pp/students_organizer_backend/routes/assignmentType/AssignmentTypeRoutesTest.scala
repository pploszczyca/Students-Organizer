package com.pp.students_organizer_backend.routes.assignmentType

import cats.effect.IO
import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.routes.assignmentType.models.response.GetAssignmentTypeResponse
import com.pp.students_organizer_backend.services.AssignmentTypeService
import io.circe.Json
import org.http4s.implicits.uri
import org.mockito.Mockito.when
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.{Method, Request, Response, Status}
import cats.effect.unsafe.implicits.global
import org.http4s.circe.jsonEncoder
import io.circe.{Decoder, Encoder}
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder

class AssignmentTypeRoutesTest extends AnyFlatSpec:
  private val assignmentTypeService = mock[AssignmentTypeService[IO]]

  "GET -> /assignmentType" should "return response" in {
    val id = 42
    val name = "name"
    val assignmentType = AssignmentTypeEntity(
      id = id,
      name = name,
    )
    val assignmentTypeResponse = GetAssignmentTypeResponse(
      id = id,
      name = name,
    )
    val expectedResponse = List(assignmentTypeResponse).asJson

    when(assignmentTypeService.getAll) thenReturn IO(List(assignmentType))

    val actualResponse = tested(assignmentTypeService = assignmentTypeService)()
      .orNotFound
      .run(Request(method = Method.GET, uri = uri"/assignmentType"))
      .unsafeRunSync()

    assert(actualResponse.status == Status.Ok)
    assert(actualResponse.as[Json].unsafeRunSync() == expectedResponse)
  }

  private def tested(assignmentTypeService: AssignmentTypeService[IO] = mock[AssignmentTypeService[IO]]): AssignmentTypeRoutes[IO] =
    AssignmentTypeRoutes(assignmentTypeService)
