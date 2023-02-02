package com.pp.students_organizer_backend.gateways.material

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.MaterialEntity
import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.routes.material.models.request.InsertMaterialRequest
import com.pp.students_organizer_backend.routes.material.models.response.GetMaterialResponse
import com.pp.students_organizer_backend.services.MaterialService
import org.mockito.ArgumentMatchers.{any, matches}
import org.mockito.Mockito.{verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock

import java.util.UUID

class MaterialRoutesGatewayTest extends AnyFlatSpec:
  private val materialService: MaterialService[IO] = mock

  "ON getAll" should "return all all materials as response" in {
    val material = mock[MaterialEntity]
    val response = mock[GetMaterialResponse]
    val mapToGetMaterialResponse = (* : MaterialEntity) => response
    val expected = List(response)

    when(materialService.getAll) thenReturn IO(List(material))

    val actual = tested(
      materialService = materialService,
      mapToGetMaterialResponse = mapToGetMaterialResponse
    ).getAll.unsafeRunSync()

    assert(actual == expected)
  }

  "ON insert" should "insert new material" in {
    val request = mock[InsertMaterialRequest]
    val material = mock[MaterialEntity]
    val mapToMaterial = (* : InsertMaterialRequest) => Right(material)

    when(materialService.insert(any())) thenReturn IO.unit

    tested(
      materialService = materialService,
      mapToMaterial = mapToMaterial
    ).insert(request)

    verify(materialService).insert(material)
  }

  "ON remove" should "remove material" in {
    val materialId = UUID.randomUUID()

    when(materialService.remove(any())) thenReturn IO.unit

    tested(
      materialService = materialService
    ).remove(materialId)

    verify(materialService).remove(materialId)
  }

  private def tested(
      materialService: MaterialService[IO] = mock,
      mapToGetMaterialResponse: MaterialEntity => GetMaterialResponse =
        (* : MaterialEntity) => mock,
      mapToMaterial: InsertMaterialRequest => Either[ValidationError, MaterialEntity] =
        (* : InsertMaterialRequest) => mock
  ): MaterialRoutesGateway[IO] =
    MaterialRoutesGateway.make(
      materialService = materialService,
      mapToGetMaterialResponse = mapToGetMaterialResponse,
      mapToMaterial = mapToMaterial
    )
