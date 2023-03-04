package com.pp.students_organizer_backend.gateways.material

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.*
import com.pp.students_organizer_backend.domain.errors.{AssignmentNotFoundException, ValidationError, ValidationException}
import com.pp.students_organizer_backend.gateways.material.mappers.{GetMaterialResponseMapper, MaterialEntityMapper}
import com.pp.students_organizer_backend.routes_models.material.request.InsertMaterialRequest
import com.pp.students_organizer_backend.routes_models.material.response.GetMaterialResponse
import com.pp.students_organizer_backend.services.{AssignmentService, MaterialService}
import org.mockito.ArgumentMatchers.{any, matches}
import org.mockito.Mockito.{inOrder, verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock

import java.util.UUID

class MaterialGatewayTest extends AnyFlatSpec:
  private val materialService: MaterialService[IO] = mock
  private val assignmentService: AssignmentService[IO] = mock
  private given mockGetMaterialResponseMapper: GetMaterialResponseMapper = mock
  private given mockMaterialEntityMapper: MaterialEntityMapper = mock

  "ON getAll" should "return all all materials as response" in {
    val studentId = mock[StudentId]
    val material = mock[MaterialEntity]
    val response = mock[GetMaterialResponse]
    val expected = List(response)

    given getMaterialResponseMapper: GetMaterialResponseMapper = mock

    when(getMaterialResponseMapper.map(any())) thenReturn response
    when(materialService.getAll(any())) thenReturn IO(List(material))

    val actual = tested(
      materialService = materialService
    ).getAll(studentId).unsafeRunSync()

    verify(getMaterialResponseMapper).map(material)
    assert(actual == expected)
  }

  "ON insert" should "throw assigment not found exception WHEN assigment is not found" in {
    val studentId = mock[StudentId]
    val request = mock[InsertMaterialRequest]
    val assignmentId = mock[AssignmentId]
    val material = fakeMaterial(
      assignmentId = assignmentId
    )
    val expectedException = AssignmentNotFoundException(assignmentId)

    given materialEntityMapper: MaterialEntityMapper = mock

    when(materialEntityMapper.map(any())) thenReturn Right(material)
    when(assignmentService.get(any(), any())) thenReturn IO(None)

    val actualException = intercept[AssignmentNotFoundException] {
      tested(
        assignmentService = assignmentService
      ).insert(request, studentId).unsafeRunSync()
    }

    val inOrderCheck = inOrder(materialEntityMapper, assignmentService)
    inOrderCheck.verify(materialEntityMapper).map(request)
    inOrderCheck.verify(assignmentService).get(assignmentId, studentId)
    assert(actualException == expectedException)
  }

  "ON insert" should "insert new material" in {
    val studentId = mock[StudentId]
    val request = mock[InsertMaterialRequest]
    val assignmentId = mock[AssignmentId]
    val material = fakeMaterial(
      assignmentId = assignmentId
    )
    val assignment = mock[AssignmentEntity]

    given materialEntityMapper: MaterialEntityMapper = mock

    when(materialEntityMapper.map(any())) thenReturn Right(material)
    when(assignmentService.get(any(), any())) thenReturn IO(Some(assignment))
    when(materialService.insert(any())) thenReturn IO.unit

    tested(
      materialService = materialService,
      assignmentService = assignmentService
    ).insert(request, studentId).unsafeRunSync()

    val inOrderCheck =
      inOrder(materialEntityMapper, assignmentService, materialService)
    inOrderCheck.verify(materialEntityMapper).map(request)
    inOrderCheck.verify(assignmentService).get(assignmentId, studentId)
    inOrderCheck.verify(materialService).insert(material)
  }

  "ON insert" should "throw validation exception WHEN mapper returned error" in {
    val studentId = mock[StudentId]
    val request = mock[InsertMaterialRequest]
    val errorMessage = "errorMessage"
    val error = ValidationError(errorMessage)
    val expectedException = ValidationException(errorMessage)

    given materialEntityMapper: MaterialEntityMapper = mock

    when(materialEntityMapper.map(any())) thenReturn Left(error)
    when(materialService.insert(any())) thenReturn IO.unit

    val actualException = intercept[ValidationException] {
      tested(
        materialService = materialService
      ).insert(request, studentId).unsafeRunSync()
    }

    verify(materialEntityMapper).map(request)
    assert(actualException == expectedException)
  }

  "ON remove" should "remove material" in {
    val studentId = mock[StudentId]
    val id = UUID.randomUUID()
    val materialId = MaterialId(id)

    when(materialService.remove(any(), any())) thenReturn IO.unit

    tested(
      materialService = materialService
    ).remove(id, studentId)

    verify(materialService).remove(materialId, studentId)
  }

  private def fakeMaterial(
      id: MaterialId = mock,
      name: MaterialName = mock,
      url: MaterialUrl = mock,
      assignmentId: AssignmentId = mock
  ) = MaterialEntity(
    id = id,
    name = name,
    url = url,
    assignmentId = assignmentId
  )

  private def tested(
      materialService: MaterialService[IO] = mock,
      assignmentService: AssignmentService[IO] = mock
  )(using
      getMaterialResponseMapper: GetMaterialResponseMapper,
      materialEntityMapper: MaterialEntityMapper
  ): MaterialGateway[IO] =
    MaterialGateway.make(
      materialService = materialService,
      assignmentService = assignmentService
    )
