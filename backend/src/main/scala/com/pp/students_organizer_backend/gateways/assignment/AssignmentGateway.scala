package com.pp.students_organizer_backend.gateways.assignment

import cats.MonadThrow
import cats.effect.kernel.Sync
import cats.syntax.all.{catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import com.pp.students_organizer_backend.domain.errors.{AssignmentNotFoundException, SubjectNotFoundException}
import com.pp.students_organizer_backend.domain.{AssignmentId, StudentId}
import com.pp.students_organizer_backend.gateways.assignment.mappers.{AssignmentEntityMapper, GetAssignmentsResponseMapper, GetSingleAssignmentResponseMapper}
import com.pp.students_organizer_backend.routes_models.assignment.request.{InsertAssignmentRequest, UpdateAssignmentRequest}
import com.pp.students_organizer_backend.routes_models.assignment.response.{GetAssignmentsResponse, GetSingleAssignmentResponse}
import com.pp.students_organizer_backend.services.{AssignmentService, MaterialService, SubjectService, TaskService}
import com.pp.students_organizer_backend.utils.NonErrorValueMapper.*
import cats.syntax.all.catsSyntaxApplicativeErrorId

import java.util.UUID

trait AssignmentGateway[F[_]]:
  def getAllBy(studentId: StudentId): F[List[GetAssignmentsResponse]]
  def getBy(
      assignmentUUID: UUID,
      studentId: StudentId
  ): F[GetSingleAssignmentResponse]
  def insert(
      request: InsertAssignmentRequest,
      studentId: StudentId
  ): F[Unit]
  def update(
      request: UpdateAssignmentRequest,
      studentId: StudentId
  ): F[Unit]
  def remove(assignmentId: UUID): F[Unit]

object AssignmentGateway:
  def make[F[_]: Sync: MonadThrow](
      assignmentService: AssignmentService[F],
      taskService: TaskService[F],
      materialService: MaterialService[F],
      subjectService: SubjectService[F],
  )(using
      assignmentEntityMapper: AssignmentEntityMapper,
      getAssignmentsResponseMapper: GetAssignmentsResponseMapper,
      getSingleAssignmentResponseMapper: GetSingleAssignmentResponseMapper
  ): AssignmentGateway[F] =
    new AssignmentGateway[F]:
      override def getAllBy(
          studentId: StudentId
      ): F[List[GetAssignmentsResponse]] =
        assignmentService
          .getAllBy(studentId = studentId)
          .map(_.map(getAssignmentsResponseMapper.map))

      override def getBy(
          assignmentUUID: UUID,
          studentId: StudentId
      ): F[GetSingleAssignmentResponse] =
        val assignmentId = AssignmentId(assignmentUUID)

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

      override def insert(
          request: InsertAssignmentRequest,
          studentId: StudentId
      ): F[Unit] =
        assignmentEntityMapper
          .map(request)
          .mapWithNoError { assignment =>
            subjectService
              .getBy(assignment.subjectId, studentId)
              .flatMap {
                case Some(_) => assignmentService.insert(assignment)
                case None => SubjectNotFoundException(assignment.subjectId).raiseError[F, Unit]
              }
          }

      override def update(
          request: UpdateAssignmentRequest,
          studentId: StudentId
      ): F[Unit] =
        assignmentEntityMapper
          .map(request)
          .mapWithNoError { assignment =>
            subjectService
              .getBy(assignment.subjectId, studentId)
              .flatMap {
                case Some(_) => assignmentService.update(assignment)
                case None => SubjectNotFoundException(assignment.subjectId).raiseError[F, Unit]
              }
          }

      override def remove(assignmentId: UUID): F[Unit] =
        assignmentService
          .remove(AssignmentId(assignmentId))
