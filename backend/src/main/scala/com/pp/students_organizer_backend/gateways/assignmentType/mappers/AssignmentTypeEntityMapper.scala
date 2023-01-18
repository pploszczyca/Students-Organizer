package com.pp.students_organizer_backend.gateways.assignmentType.mappers

import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.routes.assignmentType.models.request.InsertAssignmentTypeRequest

class AssignmentTypeEntityMapper
    extends (InsertAssignmentTypeRequest => AssignmentTypeEntity):
  override def apply(
      request: InsertAssignmentTypeRequest
  ): AssignmentTypeEntity =
    AssignmentTypeEntity(
      request.name
    )
