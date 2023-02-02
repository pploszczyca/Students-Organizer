package com.pp.students_organizer_backend.gateways.assignmentType

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.errors.{ValidationError, ValidationException}
import com.pp.students_organizer_backend.domain.{AssignmentTypeEntity, AssignmentTypeId}
import com.pp.students_organizer_backend.routes.assignmentType.models.request.InsertAssignmentTypeRequest
import com.pp.students_organizer_backend.routes.assignmentType.models.response.GetAssignmentTypeResponse
import com.pp.students_organizer_backend.services.AssignmentTypeService
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock

import java.util.UUID

class AssignmentTypeRoutesGatewayTest extends AnyFlatSpec:
  private val assignmentTypeService: AssignmentTypeService[IO] = mock

  "ON getAll" should "return all assignment types as response" in {
    val assignmentType = mock[AssignmentTypeEntity]
    val response = mock[GetAssignmentTypeResponse]
    val mapToGetAssignmentTypeResponse = (* : AssignmentTypeEntity) => response
    val expected = List(response)

    when(assignmentTypeService.getAll) thenReturn IO(List(assignmentType))

    val actual = tested(
      assignmentTypeService = assignmentTypeService,
      mapToGetAssignmentTypeResponse = mapToGetAssignmentTypeResponse
    ).getAll.unsafeRunSync()

    assert(actual == expected)
  }

  "ON insert" should "insert new assignment type" in {
    val insertRequest = mock[InsertAssignmentTypeRequest]
    val assignmentType = mock[AssignmentTypeEntity]
    val mapToAssignmentType =
      (* : InsertAssignmentTypeRequest) => Right(assignmentType)

    when(assignmentTypeService.insert(any())) thenReturn IO.unit

    tested(
      assignmentTypeService = assignmentTypeService,
      mapToAssignmentType = mapToAssignmentType
    ).insert(insertRequest).unsafeRunSync()

    verify(assignmentTypeService).insert(assignmentType)
  }

  "ON insert" should "throw validation exception WHEN mapper returned error" in {
    val insertRequest = mock[InsertAssignmentTypeRequest]
    val errorMessage = "errorMessage"
    val error = ValidationError(errorMessage)
    val mapToAssignmentType = (* : InsertAssignmentTypeRequest) => Left(error)
    val expectedException = ValidationException(errorMessage)

    when(assignmentTypeService.insert(any())) thenReturn IO.unit

    val actualException = intercept[ValidationException] {
      tested(
        assignmentTypeService = assignmentTypeService,
        mapToAssignmentType = mapToAssignmentType
      ).insert(insertRequest).unsafeRunSync()
    }

    assert(actualException == expectedException)
  }

  "ON remove" should "remove assignment type" in {
    val id = UUID.randomUUID()
    val assignmentTypeId = AssignmentTypeId(id)

    when(assignmentTypeService.remove(any())) thenReturn IO.unit

    tested(
      assignmentTypeService = assignmentTypeService,
    ).remove(id)

    verify(assignmentTypeService).remove(assignmentTypeId)
  }

  private def tested(
      assignmentTypeService: AssignmentTypeService[IO] = mock,
      mapToGetAssignmentTypeResponse: AssignmentTypeEntity => GetAssignmentTypeResponse =
        (* : AssignmentTypeEntity) => mock,
      mapToAssignmentType: InsertAssignmentTypeRequest => Either[
        ValidationError,
        AssignmentTypeEntity
      ] =
        (* : InsertAssignmentTypeRequest) => mock
  ): AssignmentTypeRoutesGateway[IO] =
    AssignmentTypeRoutesGateway.make(
      assignmentTypeService = assignmentTypeService,
      mapToGetAssignmentTypeResponse = mapToGetAssignmentTypeResponse,
      mapToAssignmentType = mapToAssignmentType
    )
