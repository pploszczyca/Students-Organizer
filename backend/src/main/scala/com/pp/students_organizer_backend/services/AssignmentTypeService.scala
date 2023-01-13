package com.pp.students_organizer_backend.services

import cats.Applicative
import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.implicits.toFunctorOps
import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import skunk.codec.all.{int4, varchar}
import skunk.implicits.{sql, toIdOps}
import skunk.{Command, Encoder, Query, Session, Void}

trait AssignmentTypeService[F[_]]:
  def getAll: F[List[AssignmentTypeEntity]]
  def insert(assignmentType: AssignmentTypeEntity): F[Unit]
  def remove(assignmentTypeId: Int): F[Unit]

object AssignmentTypeService:
  def make[F[_] : Concurrent](database: Resource[F, Session[F]]): AssignmentTypeService[F] =
    new AssignmentTypeService[F]:
      override def getAll: F[List[AssignmentTypeEntity]] =
        database.use { session =>
          session.execute(ServiceSQL.getAllQuery)
        }

      override def insert(assignmentType: AssignmentTypeEntity): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.insertCommand)
            .use(_.execute(assignmentType))
            .void
        }

      override def remove(assignmentTypeId: Int): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.removeCommand)
            .use(_.execute(assignmentTypeId))
            .void
        }

  private object ServiceSQL:
    val getAllQuery: Query[Void, AssignmentTypeEntity] =
      sql"SELECT id, name FROM assignment_type"
        .query(int4 ~ varchar)
        .gmap[AssignmentTypeEntity]

    val insertCommand: Command[AssignmentTypeEntity] =
      sql"INSERT INTO assignment_type (name) VALUES ($varchar)"
        .command
        .contramap(assignmentType => assignmentType.name)

    val removeCommand: Command[Int] =
      sql"DELETE FROM assignment_type WHERE id=$int4"
        .command
