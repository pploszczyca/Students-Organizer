package com.pp.students_organizer_backend.gateways.assignment.mappers

import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.domain.{
  AssignmentEntity,
  AssignmentTypeId,
  SubjectId
}
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

object AssignmentEntityMapper:
  given AssignmentEntityMapper with {
    override def map(
        request: InsertAssignmentRequest
    ): Either[ValidationError, AssignmentEntity] =
      AssignmentEntity.create(
        name = request.name,
        description = request.description,
        assignmentTypeId = AssignmentTypeId(request.assignmentTypeId),
        status = request.status,
        endDate = request.endDate,
        subjectId = SubjectId(request.subjectId)
      )

    override def map(
        request: UpdateAssignmentRequest
    ): Either[ValidationError, AssignmentEntity] =
      AssignmentEntity.create(
        assignmentUUID = request.id,
        name = request.name,
        description = request.description,
        assignmentTypeId = AssignmentTypeId(request.assignmentTypeId),
        status = request.status,
        endDate = request.endDate,
        subjectId = SubjectId(request.subjectId)
      )
  }
