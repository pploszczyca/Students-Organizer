package com.pp.students_organizer_backend.gateways.material

import cats.effect.kernel.Sync
import cats.syntax.all.toFunctorOps
import com.pp.students_organizer_backend.domain.MaterialEntity
import com.pp.students_organizer_backend.routes.material.models.request.InsertMaterialRequest
import com.pp.students_organizer_backend.routes.material.models.response.GetMaterialResponse
import com.pp.students_organizer_backend.services.MaterialService

trait MaterialGateway[F[_]]:
  def getAll: F[List[GetMaterialResponse]]
  def insert(request: InsertMaterialRequest): F[Unit]
  def remove(materialId: Int): F[Unit]

object MaterialGateway:
  def make[F[_]: Sync](
      materialService: MaterialService[F],
      mapToGetMaterialResponse: MaterialEntity => GetMaterialResponse,
      mapToAssignmentType: InsertMaterialRequest => MaterialEntity
  ): MaterialGateway[F] =
    new MaterialGateway[F]:
      override def getAll: F[List[GetMaterialResponse]] =
        materialService
          .getAll
          .map(_.map(mapToGetMaterialResponse))

      override def insert(request: InsertMaterialRequest): F[Unit] =
        materialService
          .insert(mapToAssignmentType(request))

      override def remove(materialId: Int): F[Unit] =
        materialService
          .remove(materialId)
