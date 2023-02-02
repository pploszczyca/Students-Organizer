package com.pp.students_organizer_backend.services

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.syntax.all.{toFlatMapOps, toFunctorOps}
import com.pp.students_organizer_backend.domain.{TaskEntity, TaskId}
import com.pp.students_organizer_backend.utils.DatabaseCodec.TaskEntity.{
  taskId,
  taskIsDone,
  taskName
}
import skunk.implicits.sql
import skunk.{Command, Query, Session, Void, ~}

trait TaskService[F[_]]:
  def getAll: F[List[TaskEntity]]
  def insert(task: TaskEntity): F[Unit]
  def remove(taskId: TaskId): F[Unit]

object TaskService:
  def make[F[_]: Concurrent](
      database: Resource[F, Session[F]]
  ): TaskService[F] =
    new TaskService[F]:
      override def getAll: F[List[TaskEntity]] =
        database.use { session =>
          session.execute(ServiceSQL.getAllQuery)
        }

      override def insert(task: TaskEntity): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.insertCommand)
            .flatMap(_.execute(task))
            .void
        }

      override def remove(taskId: TaskId): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.removeCommand)
            .flatMap(_.execute(taskId))
            .void
        }

  private object ServiceSQL:
    val getAllQuery: Query[Void, TaskEntity] =
      sql"SELECT id, name, is_done FROM task"
        .query(taskId ~ taskName ~ taskIsDone)
        .gmap[TaskEntity]

    val insertCommand: Command[TaskEntity] =
      sql"INSERT INTO task (id, name, is_done) VALUES ($taskId, $taskName, $taskIsDone)".command
        .gcontramap[TaskEntity]

    val removeCommand: Command[TaskId] =
      sql"DELETE FROM task WHERE id=$taskId".command
