package com.pp.students_organizer_backend.services

import cats.Applicative
import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.implicits.toFunctorOps
import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import skunk.codec.all.{int4, varchar}
import skunk.implicits.{sql, toIdOps}
import skunk.{Query, Session, Void}

trait AssignmentTypeService[F[_]]:
  def getAssignmentTypes: F[List[AssignmentTypeEntity]]

object AssignmentTypeService:
  def make[F[_] : Concurrent](database: Resource[F, Session[F]]): AssignmentTypeService[F] =
    new AssignmentTypeService[F]:
      override def getAssignmentTypes: F[List[AssignmentTypeEntity]] =
        database.use { session =>
          session.execute(ServiceSQL.getAssignmentTypesQuery)
        }

  private object ServiceSQL:
    val getAssignmentTypesQuery: Query[Void, AssignmentTypeEntity] =
      sql"SELECT id, name FROM assignment_type"
        .query(int4 ~ varchar)
        .gmap[AssignmentTypeEntity]
