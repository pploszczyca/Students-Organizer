package com.pp.students_organizer_backend.gateways.assignmentType

import cats.effect.Sync
import cats.syntax.all.toFunctorOps
import com.pp.students_organizer_backend.domain.{AssignmentTypeEntity, AssignmentTypeId}
import com.pp.students_organizer_backend.domain.errors.{ValidationError, ValidationException}
import com.pp.students_organizer_backend.routes.assignmentType.models.request.InsertAssignmentTypeRequest
import com.pp.students_organizer_backend.routes.assignmentType.models.response.GetAssignmentTypeResponse
import com.pp.students_organizer_backend.services.AssignmentTypeService
import cats.syntax.all.catsSyntaxApplicativeId

import java.util.UUID

trait AssignmentTypeRoutesGateway[F[_]]:
  def getAll: F[List[GetAssignmentTypeResponse]]
  def insert(insertAssignmentTypeRequest: InsertAssignmentTypeRequest): F[Unit]
  def remove(assignmentTypeId: UUID): F[Unit]

object AssignmentTypeRoutesGateway:
  def make[F[_]: Sync](
      assignmentTypeService: AssignmentTypeService[F],
      mapToGetAssignmentTypeResponse: AssignmentTypeEntity => GetAssignmentTypeResponse,
      mapToAssignmentType: InsertAssignmentTypeRequest => Either[
        ValidationError,
        AssignmentTypeEntity
      ]
  ): AssignmentTypeRoutesGateway[F] =
    new AssignmentTypeRoutesGateway[F]:
      override def getAll: F[List[GetAssignmentTypeResponse]] =
        assignmentTypeService.getAll
          .map(_.map(mapToGetAssignmentTypeResponse))

      override def insert(
          insertAssignmentTypeRequest: InsertAssignmentTypeRequest
      ): F[Unit] =
        mapToAssignmentType(insertAssignmentTypeRequest) match
          case Right(value) => assignmentTypeService.insert(value)
          case Left(ValidationError(message)) => throw ValidationException(message)

      override def remove(assignmentTypeId: UUID): F[Unit] =
        assignmentTypeService
          .remove(AssignmentTypeId(assignmentTypeId))
