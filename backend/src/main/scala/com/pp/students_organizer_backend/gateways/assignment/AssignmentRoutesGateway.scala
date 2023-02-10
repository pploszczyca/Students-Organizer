package com.pp.students_organizer_backend.gateways.assignment

import cats.effect.kernel.Sync
import com.pp.students_organizer_backend.routes_models.assignment.request.{
  InsertAssignmentRequest,
  UpdateAssignmentRequest
}
import com.pp.students_organizer_backend.routes_models.assignment.response.{
  GetAssignmentsResponse,
  GetSingleAssignmentResponse
}
import com.pp.students_organizer_backend.services.{
  AssignmentService,
  MaterialService,
  TaskService
}

import java.util.UUID

trait AssignmentRoutesGateway[F[_]]:
  def getAllBy(studentId: UUID): F[List[GetAssignmentsResponse]]
  def getBy(assignmentId: UUID, studentId: UUID): F[GetSingleAssignmentResponse]
  def insert(request: InsertAssignmentRequest): F[Unit]
  def update(request: UpdateAssignmentRequest): F[Unit]
  def remove(assignmentId: UUID): F[Unit]

object AssignmentRoutesGateway:
  def make[F[_]: Sync](
      assignmentService: AssignmentService[F],
      taskService: TaskService[F],
      materialService: MaterialService[F]
  ): AssignmentRoutesGateway[F] =
    new AssignmentRoutesGateway[F]:
      override def getAllBy(studentId: UUID): F[List[GetAssignmentsResponse]] =
        ???

      override def getBy(
          assignmentId: UUID,
          studentId: UUID
      ): F[GetSingleAssignmentResponse] = ???

      override def insert(request: InsertAssignmentRequest): F[Unit] = ???

      override def update(request: UpdateAssignmentRequest): F[Unit] = ???

      override def remove(assignmentId: UUID): F[Unit] = ???
