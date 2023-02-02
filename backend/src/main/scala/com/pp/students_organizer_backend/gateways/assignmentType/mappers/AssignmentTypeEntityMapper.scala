package com.pp.students_organizer_backend.gateways.assignmentType.mappers

import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.routes.assignmentType.models.request.InsertAssignmentTypeRequest

object AssignmentTypeEntityMapper:
  def map(
      request: InsertAssignmentTypeRequest
  ): Either[ValidationError, AssignmentTypeEntity] =
    AssignmentTypeEntity.create(
      name = request.name
    )
