package com.pp.students_organizer_backend.gateways.assignmentType.mappers

import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.routes_models.assignmentType.request.InsertAssignmentTypeRequest

trait AssignmentTypeEntityMapper:
  def map(
      request: InsertAssignmentTypeRequest
  ): Either[ValidationError, AssignmentTypeEntity]

object AssignmentTypeEntityMapper:
  given assignmentTypeEntityMapper: AssignmentTypeEntityMapper with {
    override def map(
        request: InsertAssignmentTypeRequest
    ): Either[ValidationError, AssignmentTypeEntity] =
      AssignmentTypeEntity.create(
        name = request.name
      )
  }
