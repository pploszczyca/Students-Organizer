package com.pp.students_organizer_backend.gateways.assignment

import cats.effect.kernel.Sync
import cats.syntax.all.{catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import com.pp.students_organizer_backend.domain.errors.AssignmentNotFoundException
import com.pp.students_organizer_backend.domain.{AssignmentId, StudentId}
import com.pp.students_organizer_backend.gateways.assignment.mappers.{AssignmentEntityMapper, GetAssignmentsResponseMapper, GetSingleAssignmentResponseMapper}
import com.pp.students_organizer_backend.routes_models.assignment.request.{InsertAssignmentRequest, UpdateAssignmentRequest}
import com.pp.students_organizer_backend.routes_models.assignment.response.{GetAssignmentsResponse, GetSingleAssignmentResponse}
import com.pp.students_organizer_backend.services.{AssignmentService, MaterialService, TaskService}
import com.pp.students_organizer_backend.utils.NonErrorValueMapper.*

import java.util.UUID

trait AssignmentGateway[F[_]]:
  def getAllBy(studentUUID: UUID): F[List[GetAssignmentsResponse]]
  def getBy(
      assignmentUUID: UUID,
      studentUUID: UUID
  ): F[GetSingleAssignmentResponse]
  def insert(request: InsertAssignmentRequest): F[Unit]
  def update(request: UpdateAssignmentRequest): F[Unit]
  def remove(assignmentId: UUID): F[Unit]

object AssignmentGateway:
  def make[F[_]: Sync](
      assignmentService: AssignmentService[F],
      taskService: TaskService[F],
      materialService: MaterialService[F]
  )(using
      assignmentEntityMapper: AssignmentEntityMapper,
      getAssignmentsResponseMapper: GetAssignmentsResponseMapper,
      getSingleAssignmentResponseMapper: GetSingleAssignmentResponseMapper
  ): AssignmentGateway[F] =
    new AssignmentGateway[F]:
      override def getAllBy(
          studentUUID: UUID
      ): F[List[GetAssignmentsResponse]] =
        assignmentService
          .getAllBy(studentId = StudentId(studentUUID))
          .map(_.map(getAssignmentsResponseMapper.map))

      override def getBy(
          assignmentUUID: UUID,
          studentUUID: UUID
      ): F[GetSingleAssignmentResponse] =
        val assignmentId = AssignmentId(assignmentUUID)
        val studentId = StudentId(studentUUID)

        for
          assignment <- assignmentService.get(assignmentId, studentId)
          tasks <- taskService.getAllBy(assignmentId)
          materials <- materialService.getAllBy(assignmentId)
        yield assignment match
          case Some(value) =>
            getSingleAssignmentResponseMapper.map(
              assignment = value,
              tasks = tasks,
              materials = materials
            )
          case None => throw AssignmentNotFoundException

      override def insert(request: InsertAssignmentRequest): F[Unit] =
        assignmentEntityMapper
          .map(request)
          .mapWithNoError(assignmentService.insert)

      override def update(request: UpdateAssignmentRequest): F[Unit] =
        assignmentEntityMapper
          .map(request)
          .mapWithNoError(assignmentService.update)

      override def remove(assignmentId: UUID): F[Unit] =
        assignmentService
          .remove(AssignmentId(assignmentId))
