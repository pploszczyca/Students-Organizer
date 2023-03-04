package com.pp.students_organizer_backend.gateways.assignmentType

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.errors.{ValidationError, ValidationException}
import com.pp.students_organizer_backend.domain.{AssignmentTypeEntity, AssignmentTypeId}
import com.pp.students_organizer_backend.gateways.assignmentType.mappers.{AssignmentTypeEntityMapper, GetAssignmentTypeResponseMapper}
import com.pp.students_organizer_backend.routes_models.assignmentType.request.InsertAssignmentTypeRequest
import com.pp.students_organizer_backend.routes_models.assignmentType.response.GetAssignmentTypeResponse
import com.pp.students_organizer_backend.services.AssignmentTypeService
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{inOrder, verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock

import java.util.UUID

class AssignmentTypeGatewayTest extends AnyFlatSpec:
  private val assignmentTypeService: AssignmentTypeService[IO] = mock
  private given getAssignmentTypeResponseMapper
      : GetAssignmentTypeResponseMapper = mock
  private given assignmentTypeEntityMapper: AssignmentTypeEntityMapper = mock

  "ON getAll" should "return all assignment types as response" in {
    val assignmentType = mock[AssignmentTypeEntity]
    val response = mock[GetAssignmentTypeResponse]
    val expected = List(response)

    given getAssignmentTypeResponseMapper: GetAssignmentTypeResponseMapper =
      mock

    when(getAssignmentTypeResponseMapper.map(any())) thenReturn response
    when(assignmentTypeService.getAll) thenReturn IO(List(assignmentType))

    val actual = tested(
      assignmentTypeService = assignmentTypeService
    ).getAll.unsafeRunSync()

    verify(getAssignmentTypeResponseMapper).map(assignmentType)
    assert(actual == expected)
  }

  "ON insert" should "insert new assignment type" in {
    val insertRequest = mock[InsertAssignmentTypeRequest]
    val assignmentType = mock[AssignmentTypeEntity]

    given assignmentTypeEntityMapper: AssignmentTypeEntityMapper = mock

    when(assignmentTypeEntityMapper.map(any())) thenReturn Right(assignmentType)
    when(assignmentTypeService.insert(any())) thenReturn IO.unit

    tested(
      assignmentTypeService = assignmentTypeService
    ).insert(insertRequest).unsafeRunSync()

    val inOrderCheck =
      inOrder(assignmentTypeEntityMapper, assignmentTypeService)
    inOrderCheck.verify(assignmentTypeEntityMapper).map(insertRequest)
    inOrderCheck.verify(assignmentTypeService).insert(assignmentType)
  }

  "ON insert" should "throw validation exception WHEN mapper returned error" in {
    val insertRequest = mock[InsertAssignmentTypeRequest]
    val errorMessage = "errorMessage"
    val error = ValidationError(errorMessage)
    val expectedException = ValidationException(errorMessage)

    given assignmentTypeEntityMapper: AssignmentTypeEntityMapper = mock

    when(assignmentTypeEntityMapper.map(any())) thenReturn Left(error)
    when(assignmentTypeService.insert(any())) thenReturn IO.unit

    val actualException = intercept[ValidationException] {
      tested(
        assignmentTypeService = assignmentTypeService
      ).insert(insertRequest).unsafeRunSync()
    }

    verify(assignmentTypeEntityMapper).map(insertRequest)
    assert(actualException == expectedException)
  }

  "ON remove" should "remove assignment type" in {
    val id = UUID.randomUUID()
    val assignmentTypeId = AssignmentTypeId(id)

    when(assignmentTypeService.remove(any())) thenReturn IO.unit

    tested(
      assignmentTypeService = assignmentTypeService
    ).remove(id)

    verify(assignmentTypeService).remove(assignmentTypeId)
  }

  private def tested(
      assignmentTypeService: AssignmentTypeService[IO] = mock
  )(using
      assignmentTypeEntityMapper: AssignmentTypeEntityMapper,
      getAssignmentTypeResponseMapper: GetAssignmentTypeResponseMapper
  ): AssignmentTypeGateway[IO] =
    AssignmentTypeGateway.make(
      assignmentTypeService = assignmentTypeService
    )
