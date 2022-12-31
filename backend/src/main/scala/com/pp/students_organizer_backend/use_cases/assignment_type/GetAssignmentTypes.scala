package com.pp.students_organizer_backend.use_cases.assignment_type

import cats.effect.kernel.Concurrent
import cats.implicits.*
import cats.syntax.flatMap.*
import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.services.database.AssignmentTypeService
import com.pp.students_organizer_backend.services.database.models.AssignmentTypeModel.mapToAssignmentTypeEntity

class GetAssignmentTypes[F[_] : Concurrent](private val assignmentTypeService: AssignmentTypeService[F]) 
  extends (() => F[List[AssignmentTypeEntity]]):
  
  override def apply(): F[List[AssignmentTypeEntity]] =
    assignmentTypeService
      .getAssignmentTypes
      .map(_.map(mapToAssignmentTypeEntity))

