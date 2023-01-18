package com.pp.students_organizer_backend.gateways.assignmentType

import cats.effect.Sync
import cats.syntax.all.toFunctorOps
import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.routes.assignmentType.models.request.InsertAssignmentTypeRequest
import com.pp.students_organizer_backend.routes.assignmentType.models.response.GetAssignmentTypeResponse
import com.pp.students_organizer_backend.services.AssignmentTypeService

trait AssignmentTypeRoutesGateway[F[_]]:
  def getAll: F[List[GetAssignmentTypeResponse]]
  def insert(insertAssignmentTypeRequest: InsertAssignmentTypeRequest): F[Unit]
  def remove(assignmentTypeId: Int): F[Unit]

object AssignmentTypeRoutesGateway:
  def make[F[_]: Sync](
      assignmentTypeService: AssignmentTypeService[F],
      mapToGetAssignmentTypeResponse: AssignmentTypeEntity => GetAssignmentTypeResponse,
      mapToAssignmentType: InsertAssignmentTypeRequest => AssignmentTypeEntity,
  ): AssignmentTypeRoutesGateway[F] =
    new AssignmentTypeRoutesGateway[F]:
      override def getAll: F[List[GetAssignmentTypeResponse]] =
        assignmentTypeService
          .getAll
          .map(_.map(mapToGetAssignmentTypeResponse))

      override def insert(
          insertAssignmentTypeRequest: InsertAssignmentTypeRequest
      ): F[Unit] =
        assignmentTypeService
          .insert(mapToAssignmentType(insertAssignmentTypeRequest))

      override def remove(assignmentTypeId: Int): F[Unit] =
        assignmentTypeService
          .remove(assignmentTypeId)
