package com.pp.students_organizer_backend.gateways.material

import cats.effect.kernel.Sync
import cats.syntax.all.toFunctorOps
import com.pp.students_organizer_backend.domain.MaterialEntity
import com.pp.students_organizer_backend.domain.common.ValidationError
import com.pp.students_organizer_backend.routes.material.models.request.InsertMaterialRequest
import com.pp.students_organizer_backend.routes.material.models.response.GetMaterialResponse
import com.pp.students_organizer_backend.services.MaterialService

import java.util.UUID

trait MaterialRoutesGateway[F[_]]:
  def getAll: F[List[GetMaterialResponse]]
  def insert(request: InsertMaterialRequest): Either[ValidationError, F[Unit]]
  def remove(materialId: UUID): F[Unit]

object MaterialRoutesGateway:
  def make[F[_]: Sync](
      materialService: MaterialService[F],
      mapToGetMaterialResponse: MaterialEntity => GetMaterialResponse,
      mapToMaterial: InsertMaterialRequest => Either[ValidationError, MaterialEntity]
  ): MaterialRoutesGateway[F] =
    new MaterialRoutesGateway[F]:
      override def getAll: F[List[GetMaterialResponse]] =
        materialService.getAll
          .map(_.map(mapToGetMaterialResponse))

      override def insert(request: InsertMaterialRequest): Either[ValidationError, F[Unit]] =
        mapToMaterial(request)
          .map(materialService.insert)

      override def remove(materialId: UUID): F[Unit] =
        materialService
          .remove(materialId)
