package com.pp.students_organizer_backend.services.database

import cats.Applicative
import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.implicits.toFunctorOps
import com.pp.students_organizer_backend.services.database.models.AssignmentTypeModel
import skunk.codec.all.{int4, varchar}
import skunk.implicits.{sql, toIdOps}
import skunk.{Query, Session, Void}

class AssignmentTypeService[F[_] : Concurrent](private val database: Resource[F, Session[F]]):
  private val getAssignmentTypesQuery: Query[Void, AssignmentTypeModel] =
    sql"SELECT id, name FROM assignment_type"
      .query(int4 ~ varchar)
      .gmap[AssignmentTypeModel]


  def getAssignmentTypes: F[List[AssignmentTypeModel]] =
    database.use { session =>
      session.execute(getAssignmentTypesQuery)
    }
