package com.pp.students_organizer_backend.gateways.material

import cats.MonadThrow
import cats.effect.kernel.Sync
import cats.syntax.all.{catsSyntaxApplicativeErrorId, toFlatMapOps, toFunctorOps}
import com.pp.students_organizer_backend.domain.errors.{AssignmentNotFoundException, ValidationError, ValidationException}
import com.pp.students_organizer_backend.domain.{MaterialEntity, MaterialId, StudentId}
import com.pp.students_organizer_backend.gateways.material.mappers.{GetMaterialResponseMapper, MaterialEntityMapper}
import com.pp.students_organizer_backend.routes_models.material.request.InsertMaterialRequest
import com.pp.students_organizer_backend.routes_models.material.response.GetMaterialResponse
import com.pp.students_organizer_backend.services.{AssignmentService, MaterialService}
import com.pp.students_organizer_backend.utils.NonErrorValueMapper.*

import java.util.UUID

trait MaterialGateway[F[_]]:
  def getAll(studentId: StudentId): F[List[GetMaterialResponse]]
  def insert(request: InsertMaterialRequest, studentId: StudentId): F[Unit]
  def remove(materialId: UUID, studentId: StudentId): F[Unit]

object MaterialGateway:
  def make[F[_]: Sync: MonadThrow](
      materialService: MaterialService[F],
      assignmentService: AssignmentService[F]
  )(using
      getMaterialResponseMapper: GetMaterialResponseMapper,
      materialEntityMapper: MaterialEntityMapper
  ): MaterialGateway[F] =
    new MaterialGateway[F]:
      override def getAll(studentId: StudentId): F[List[GetMaterialResponse]] =
        materialService
          .getAll(studentId)
          .map(_.map(getMaterialResponseMapper.map))

      override def insert(
          request: InsertMaterialRequest,
          studentId: StudentId
      ): F[Unit] =
        materialEntityMapper
          .map(request)
          .mapWithNoError { material =>
            assignmentService
              .get(material.assignmentId, studentId)
              .flatMap {
                case Some(_) => materialService.insert(material)
                case None =>
                  AssignmentNotFoundException(material.assignmentId)
                    .raiseError[F, Unit]
              }
          }

      override def remove(
          materialId: UUID,
          studentId: StudentId
      ): F[Unit] =
        materialService
          .remove(MaterialId(materialId), studentId)
