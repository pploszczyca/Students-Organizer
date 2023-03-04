package com.pp.students_organizer_backend.gateways.task

import cats.effect.kernel.Sync
import cats.syntax.all.{catsSyntaxApplicativeErrorId, toFlatMapOps, toFunctorOps}
import com.pp.students_organizer_backend.domain.errors.{AssignmentNotFoundException, ValidationError, ValidationException}
import com.pp.students_organizer_backend.domain.{StudentId, TaskEntity, TaskId}
import com.pp.students_organizer_backend.gateways.task.mappers.{GetTaskResponseMapper, TaskEntityMapper}
import com.pp.students_organizer_backend.routes_models.task.request.InsertTaskRequest
import com.pp.students_organizer_backend.routes_models.task.response.GetTaskResponse
import com.pp.students_organizer_backend.services.{AssignmentService, TaskService}
import com.pp.students_organizer_backend.utils.NonErrorValueMapper.*

import java.util.UUID

trait TaskGateway[F[_]]:
  def getAll(studentId: StudentId): F[List[GetTaskResponse]]
  def insert(request: InsertTaskRequest, studentId: StudentId): F[Unit]
  def remove(taskId: UUID, studentId: StudentId): F[Unit]

object TaskGateway:
  def make[F[_]: Sync](
      taskService: TaskService[F],
      assignmentService: AssignmentService[F]
  )(using
      getTaskResponseMapper: GetTaskResponseMapper,
      taskEntityMapper: TaskEntityMapper
  ): TaskGateway[F] = new TaskGateway[F]:
    override def getAll(studentId: StudentId): F[List[GetTaskResponse]] =
      taskService
        .getAll(studentId)
        .map(_.map(getTaskResponseMapper.map))

    override def insert(
        request: InsertTaskRequest,
        studentId: StudentId
    ): F[Unit] =
      taskEntityMapper
        .map(request)
        .mapWithNoError { task =>
          assignmentService
            .get(task.assignmentId, studentId)
            .flatMap {
              case Some(_) => taskService.insert(task)
              case None =>
                AssignmentNotFoundException(task.assignmentId)
                  .raiseError[F, Unit]
            }
        }

    override def remove(
        taskId: UUID,
        studentId: StudentId
    ): F[Unit] =
      taskService
        .remove(TaskId(taskId), studentId)
