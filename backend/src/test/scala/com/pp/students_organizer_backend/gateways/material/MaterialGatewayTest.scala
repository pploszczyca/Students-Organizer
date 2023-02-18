package com.pp.students_organizer_backend.gateways.material

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.errors.{ValidationError, ValidationException}
import com.pp.students_organizer_backend.domain.{MaterialEntity, MaterialId}
import com.pp.students_organizer_backend.gateways.material.mappers.{GetMaterialResponseMapper, MaterialEntityMapper}
import com.pp.students_organizer_backend.routes_models.material.request.InsertMaterialRequest
import com.pp.students_organizer_backend.routes_models.material.response.GetMaterialResponse
import com.pp.students_organizer_backend.services.MaterialService
import org.mockito.ArgumentMatchers.{any, matches}
import org.mockito.Mockito.{inOrder, verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock

import java.util.UUID

class MaterialGatewayTest extends AnyFlatSpec:
  private val materialService: MaterialService[IO] = mock
  private given mockGetMaterialResponseMapper: GetMaterialResponseMapper = mock
  private given mockMaterialEntityMapper: MaterialEntityMapper = mock

  "ON getAll" should "return all all materials as response" in {
    val material = mock[MaterialEntity]
    val response = mock[GetMaterialResponse]
    val expected = List(response)

    given getMaterialResponseMapper: GetMaterialResponseMapper = mock

    when(getMaterialResponseMapper.map(any())) thenReturn response
    when(materialService.getAll) thenReturn IO(List(material))

    val actual = tested(
      materialService = materialService,
    ).getAll.unsafeRunSync()

    verify(getMaterialResponseMapper).map(material)
    assert(actual == expected)
  }

  "ON insert" should "insert new material" in {
    val request = mock[InsertMaterialRequest]
    val material = mock[MaterialEntity]

    given materialEntityMapper: MaterialEntityMapper = mock

    when(materialEntityMapper.map(any())) thenReturn Right(material)
    when(materialService.insert(any())) thenReturn IO.unit

    tested(
      materialService = materialService,
    ).insert(request)

    val inOrderCheck = inOrder(materialEntityMapper, materialService)
    inOrderCheck.verify(materialEntityMapper).map(request)
    inOrderCheck.verify(materialService).insert(material)
  }

  "ON insert" should "throw validation exception WHEN mapper returned error" in {
    val request = mock[InsertMaterialRequest]
    val errorMessage = "errorMessage"
    val error = ValidationError(errorMessage)
    val expectedException = ValidationException(errorMessage)

    given materialEntityMapper: MaterialEntityMapper = mock

    when(materialEntityMapper.map(any())) thenReturn Left(error)
    when(materialService.insert(any())) thenReturn IO.unit

    val actualException = intercept[ValidationException] {
      tested(
        materialService = materialService,
      ).insert(request)
    }

    verify(materialEntityMapper).map(request)
    assert(actualException == expectedException)
  }

  "ON remove" should "remove material" in {
    val id = UUID.randomUUID()
    val materialId = MaterialId(id)

    when(materialService.remove(any())) thenReturn IO.unit

    tested(
      materialService = materialService
    ).remove(id)

    verify(materialService).remove(materialId)
  }

  private def tested(
      materialService: MaterialService[IO] = mock
  )(using
      getMaterialResponseMapper: GetMaterialResponseMapper,
      materialEntityMapper: MaterialEntityMapper
  ): MaterialGateway[IO] =
    MaterialGateway.make(
      materialService = materialService
    )
