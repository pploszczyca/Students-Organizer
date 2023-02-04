package com.pp.students_organizer_backend.gateways.assignmentType

import cats.effect.Sync
import cats.syntax.all.{catsSyntaxApplicativeId, toFunctorOps}
import com.pp.students_organizer_backend.domain.errors.{
  ValidationError,
  ValidationException
}
import com.pp.students_organizer_backend.domain.{
  AssignmentTypeEntity,
  AssignmentTypeId
}
import com.pp.students_organizer_backend.gateways.assignmentType.mappers.{
  AssignmentTypeEntityMapper,
  GetAssignmentTypeResponseMapper
}
import com.pp.students_organizer_backend.routes_models.assignmentType.request.InsertAssignmentTypeRequest
import com.pp.students_organizer_backend.routes_models.assignmentType.response.GetAssignmentTypeResponse
import com.pp.students_organizer_backend.services.AssignmentTypeService
import com.pp.students_organizer_backend.utils.NonErrorValueMapper.*

import java.util.UUID

trait AssignmentTypeRoutesGateway[F[_]]:
  def getAll: F[List[GetAssignmentTypeResponse]]
  def insert(insertAssignmentTypeRequest: InsertAssignmentTypeRequest): F[Unit]
  def remove(assignmentTypeId: UUID): F[Unit]

object AssignmentTypeRoutesGateway:
  def make[F[_]: Sync](
      assignmentTypeService: AssignmentTypeService[F]
  )(using
      assignmentTypeEntityMapper: AssignmentTypeEntityMapper,
      getAssignmentTypeResponseMapper: GetAssignmentTypeResponseMapper
  ): AssignmentTypeRoutesGateway[F] =
    new AssignmentTypeRoutesGateway[F]:
      override def getAll: F[List[GetAssignmentTypeResponse]] =
        assignmentTypeService.getAll
          .map(_.map(getAssignmentTypeResponseMapper.map))

      override def insert(
          insertAssignmentTypeRequest: InsertAssignmentTypeRequest
      ): F[Unit] =
        assignmentTypeEntityMapper
          .map(insertAssignmentTypeRequest)
          .mapWithNoError(assignmentTypeService.insert)

      override def remove(assignmentTypeId: UUID): F[Unit] =
        assignmentTypeService
          .remove(AssignmentTypeId(assignmentTypeId))
