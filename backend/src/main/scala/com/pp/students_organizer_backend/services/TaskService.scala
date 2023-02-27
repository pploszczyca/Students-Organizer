package com.pp.students_organizer_backend.services

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.syntax.all.{toFlatMapOps, toFunctorOps}
import com.pp.students_organizer_backend.domain.{AssignmentId, StudentId, TaskEntity, TaskId}
import com.pp.students_organizer_backend.services.database.DatabaseCodec.Assignment.assignmentId
import com.pp.students_organizer_backend.services.database.DatabaseCodec.Student.studentId
import com.pp.students_organizer_backend.services.database.DatabaseCodec.TaskEntity.{taskId, taskIsDone, taskName}
import skunk.implicits.sql
import skunk.{Command, Query, Session, Void, ~}

trait TaskService[F[_]]:
  def getAll(studentId: StudentId): F[List[TaskEntity]]
  def getAllBy(assignmentId: AssignmentId): F[List[TaskEntity]]
  def insert(task: TaskEntity): F[Unit]
  def remove(taskId: TaskId, studentId: StudentId): F[Unit]

object TaskService:
  def make[F[_]: Concurrent](
      database: Resource[F, Session[F]]
  ): TaskService[F] =
    new TaskService[F]:
      override def getAll(studentId: StudentId): F[List[TaskEntity]] =
        database.use { session =>
          session
            .prepare(ServiceSQL.getAllQuery)
            .flatMap(_.stream(studentId, 1024).compile.toList)
        }

      override def getAllBy(assignmentId: AssignmentId): F[List[TaskEntity]] =
        database.use { session =>
          session
            .prepare(ServiceSQL.getAllByAssignmentIdQuery)
            .flatMap(_.stream(assignmentId, 1024).compile.toList)
        }

      override def insert(task: TaskEntity): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.insertCommand)
            .flatMap(_.execute(task))
            .void
        }

      override def remove(taskId: TaskId, studentId: StudentId): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.removeCommand)
            .flatMap(_.execute(taskId, studentId))
            .void
        }

  private object ServiceSQL:
    val getAllQuery: Query[StudentId, TaskEntity] =
      sql"""SELECT id, name, is_done, assignment_id FROM task_with_student
           WHERE student_id = $studentId
         """
        .query(taskId ~ taskName ~ taskIsDone ~ assignmentId)
        .gmap[TaskEntity]

    val getAllByAssignmentIdQuery: Query[AssignmentId, TaskEntity] =
      sql"SELECT id, name, is_done, assignment_id FROM task WHERE assignment_id = $assignmentId"
        .query(taskId ~ taskName ~ taskIsDone ~ assignmentId)
        .gmap[TaskEntity]

    val insertCommand: Command[TaskEntity] =
      sql"INSERT INTO task (id, name, is_done, assignment_id) VALUES ($taskId, $taskName, $taskIsDone, $assignmentId)".command
        .gcontramap[TaskEntity]

    val removeCommand: Command[TaskId ~ StudentId] =
      sql"""DELETE FROM task t
       USING task_with_student tws
       WHERE t.id=$taskId AND t.id=tws.id AND tws.student_id=$studentId""".command
