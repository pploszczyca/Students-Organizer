package com.pp.students_organizer_backend.gateways.task

import cats.effect.kernel.Sync
import cats.syntax.all.toFunctorOps
import com.pp.students_organizer_backend.domain.errors.{ValidationError, ValidationException}
import com.pp.students_organizer_backend.domain.{TaskEntity, TaskId}
import com.pp.students_organizer_backend.gateways.task.mappers.{GetTaskResponseMapper, TaskEntityMapper}
import com.pp.students_organizer_backend.routes_models.task.request.InsertTaskRequest
import com.pp.students_organizer_backend.routes_models.task.response.GetTaskResponse
import com.pp.students_organizer_backend.services.TaskService

import java.util.UUID

trait TaskRoutesGateway[F[_]]:
  def getAll: F[List[GetTaskResponse]]
  def insert(request: InsertTaskRequest): F[Unit]
  def remove(taskId: UUID): F[Unit]

object TaskRoutesGateway:
  def make[F[_]: Sync](
      taskService: TaskService[F]
  )(using
      getTaskResponseMapper: GetTaskResponseMapper,
      taskEntityMapper: TaskEntityMapper
  ): TaskRoutesGateway[F] = new TaskRoutesGateway[F]:
    override def getAll: F[List[GetTaskResponse]] =
      taskService
        .getAll
        .map(_.map(getTaskResponseMapper.map))

    override def insert(request: InsertTaskRequest): F[Unit] =
      taskEntityMapper.map(request) match
        case Right(value) => taskService.insert(value)
        case Left(ValidationError(message)) =>
          throw ValidationException(message)

    override def remove(taskId: UUID): F[Unit] =
      taskService
        .remove(TaskId(taskId))
