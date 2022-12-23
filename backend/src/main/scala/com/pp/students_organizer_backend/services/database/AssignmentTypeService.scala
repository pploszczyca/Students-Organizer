package com.pp.students_organizer_backend.services.database

import cats.Applicative
import com.pp.students_organizer_backend.services.database.models.AssignmentTypeModel
import skunk.codec.all.{int4, varchar}
import skunk.implicits.{sql, toIdOps}
import skunk.{Query, Session, Void}
import cats.implicits.toFunctorOps

class AssignmentTypeService[F[_] : Applicative](private val session: Session[F]):
  private val getAssignmentTypesQuery: Query[Void, AssignmentTypeModel] =
    sql"SELECT id, name FROM assignment_type"
      .query(int4 ~ varchar)
      .gmap[AssignmentTypeModel]


  def getAssignmentTypes(): F[List[AssignmentTypeModel]] =
    session.execute(query = getAssignmentTypesQuery)


