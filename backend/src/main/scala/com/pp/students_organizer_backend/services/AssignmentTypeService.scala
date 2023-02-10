package com.pp.students_organizer_backend.services

import cats.Applicative
import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.implicits.toFunctorOps
import cats.syntax.all.toFlatMapOps
import com.pp.students_organizer_backend.domain.{AssignmentTypeEntity, AssignmentTypeId}
import com.pp.students_organizer_backend.services.database.DatabaseCodec.AssignmentType.{assignmentTypeId, assignmentTypeName}
import skunk.codec.all.{int4, varchar}
import skunk.implicits.{sql, toIdOps}
import skunk.{Command, Query, Session, Void}

trait AssignmentTypeService[F[_]]:
  def getAll: F[List[AssignmentTypeEntity]]
  def insert(assignmentType: AssignmentTypeEntity): F[Unit]
  def remove(assignmentTypeId: AssignmentTypeId): F[Unit]

object AssignmentTypeService:
  def make[F[_]: Concurrent](
      database: Resource[F, Session[F]]
  ): AssignmentTypeService[F] =
    new AssignmentTypeService[F]:
      override def getAll: F[List[AssignmentTypeEntity]] =
        database.use { session =>
          session.execute(ServiceSQL.getAllQuery)
        }

      override def insert(assignmentType: AssignmentTypeEntity): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.insertCommand)
            .flatMap(_.execute(assignmentType))
            .void
        }

      override def remove(assignmentTypeId: AssignmentTypeId): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.removeCommand)
            .flatMap(_.execute(assignmentTypeId))
            .void
        }

  private object ServiceSQL:
    val getAllQuery: Query[Void, AssignmentTypeEntity] =
      sql"SELECT id, name FROM assignment_type"
        .query(assignmentTypeId ~ assignmentTypeName)
        .gmap[AssignmentTypeEntity]

    val insertCommand: Command[AssignmentTypeEntity] =
      sql"INSERT INTO assignment_type (id, name) VALUES ($assignmentTypeId, $assignmentTypeName)".command
        .gcontramap[AssignmentTypeEntity]

    val removeCommand: Command[AssignmentTypeId] =
      sql"DELETE FROM assignment_type WHERE id=$assignmentTypeId".command
