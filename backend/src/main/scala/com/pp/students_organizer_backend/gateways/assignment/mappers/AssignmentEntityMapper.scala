package com.pp.students_organizer_backend.gateways.assignment.mappers

import com.pp.students_organizer_backend.domain.AssignmentEntity
import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.routes_models.assignment.request.{
  InsertAssignmentRequest,
  UpdateAssignmentRequest
}

trait AssignmentEntityMapper:
  def map(
      request: InsertAssignmentRequest
  ): Either[ValidationError, AssignmentEntity]

  def map(
      request: UpdateAssignmentRequest
  ): Either[ValidationError, AssignmentEntity]
