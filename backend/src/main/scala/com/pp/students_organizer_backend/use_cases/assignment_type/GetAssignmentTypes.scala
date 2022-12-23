package com.pp.students_organizer_backend.use_cases.assignment_type

import cats.Applicative
import cats.implicits.*
import cats.syntax.flatMap.*
import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.services.database.AssignmentTypeService
import com.pp.students_organizer_backend.use_cases.assignment_type.mappers.AssignmentTypeMapper

class GetAssignmentTypes[F[_] : Applicative](private val assignmentTypeService: AssignmentTypeService[F],
                                             private val assignmentTypeMapper: AssignmentTypeMapper,
                                            ) extends (() => F[List[AssignmentTypeEntity]]):
  override def apply(): F[List[AssignmentTypeEntity]] =
    assignmentTypeService
      .getAssignmentTypes()
      .map(_.map(assignmentTypeMapper.map))
