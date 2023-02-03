package com.pp.students_organizer_backend.gateways.material

import cats.effect.kernel.Sync
import cats.syntax.all.toFunctorOps
import com.pp.students_organizer_backend.domain.errors.{
  ValidationError,
  ValidationException
}
import com.pp.students_organizer_backend.domain.{MaterialEntity, MaterialId}
import com.pp.students_organizer_backend.gateways.material.mappers.{
  GetMaterialResponseMapper,
  MaterialEntityMapper
}
import com.pp.students_organizer_backend.routes.material.models.request.InsertMaterialRequest
import com.pp.students_organizer_backend.routes.material.models.response.GetMaterialResponse
import com.pp.students_organizer_backend.services.MaterialService

import java.util.UUID

trait MaterialRoutesGateway[F[_]]:
  def getAll: F[List[GetMaterialResponse]]
  def insert(request: InsertMaterialRequest): F[Unit]
  def remove(materialId: UUID): F[Unit]

object MaterialRoutesGateway:
  def make[F[_]: Sync](
      materialService: MaterialService[F]
  )(using
      getMaterialResponseMapper: GetMaterialResponseMapper,
      materialEntityMapper: MaterialEntityMapper
  ): MaterialRoutesGateway[F] =
    new MaterialRoutesGateway[F]:
      override def getAll: F[List[GetMaterialResponse]] =
        materialService.getAll
          .map(_.map(getMaterialResponseMapper.map))

      override def insert(request: InsertMaterialRequest): F[Unit] =
        materialEntityMapper.map(request) match
          case Right(value) => materialService.insert(value)
          case Left(ValidationError(message)) =>
            throw ValidationException(message)

      override def remove(materialId: UUID): F[Unit] =
        materialService
          .remove(MaterialId(materialId))
