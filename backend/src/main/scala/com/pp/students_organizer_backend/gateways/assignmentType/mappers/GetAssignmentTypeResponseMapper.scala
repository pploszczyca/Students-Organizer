package com.pp.students_organizer_backend.gateways.assignmentType.mappers

import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.routes.assignmentType.models.response.GetAssignmentTypeResponse

class GetAssignmentTypeResponseMapper
    extends (AssignmentTypeEntity => GetAssignmentTypeResponse):
  override def apply(
      assignmentType: AssignmentTypeEntity
  ): GetAssignmentTypeResponse =
    GetAssignmentTypeResponse(
      id = assignmentType.id,
      name = assignmentType.name
    )
