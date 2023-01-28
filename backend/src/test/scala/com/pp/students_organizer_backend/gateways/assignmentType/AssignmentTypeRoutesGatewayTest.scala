package com.pp.students_organizer_backend.gateways.assignmentType

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.routes.assignmentType.models.request.InsertAssignmentTypeRequest
import com.pp.students_organizer_backend.routes.assignmentType.models.response.GetAssignmentTypeResponse
import com.pp.students_organizer_backend.services.AssignmentTypeService
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock

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
      (* : InsertAssignmentTypeRequest) => assignmentType

    when(assignmentTypeService.insert(any())) thenReturn IO.unit

    tested(
      assignmentTypeService = assignmentTypeService,
      mapToAssignmentType = mapToAssignmentType
    ).insert(insertRequest).unsafeRunSync()

    verify(assignmentTypeService).insert(assignmentType)
  }

  "ON remove" should "remove assignment type" in {
    val assignmentTypeId = 99

    when(assignmentTypeService.remove(any())) thenReturn IO.unit

    tested(
      assignmentTypeService = assignmentTypeService,
    ).remove(assignmentTypeId)

    verify(assignmentTypeService).remove(assignmentTypeId)
  }

  private def tested(
      assignmentTypeService: AssignmentTypeService[IO] = mock,
      mapToGetAssignmentTypeResponse: AssignmentTypeEntity => GetAssignmentTypeResponse =
        (* : AssignmentTypeEntity) => mock,
      mapToAssignmentType: InsertAssignmentTypeRequest => AssignmentTypeEntity =
        (* : InsertAssignmentTypeRequest) => mock
  ): AssignmentTypeRoutesGateway[IO] =
    AssignmentTypeRoutesGateway.make(
      assignmentTypeService = assignmentTypeService,
      mapToGetAssignmentTypeResponse = mapToGetAssignmentTypeResponse,
      mapToAssignmentType = mapToAssignmentType
    )
