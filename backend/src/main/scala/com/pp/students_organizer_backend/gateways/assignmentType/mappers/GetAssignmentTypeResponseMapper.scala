package com.pp.students_organizer_backend.gateways.assignmentType.mappers

import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.routes.assignmentType.models.response.GetAssignmentTypeResponse

object GetAssignmentTypeResponseMapper:
  def map(
      assignmentType: AssignmentTypeEntity
  ): GetAssignmentTypeResponse =
    GetAssignmentTypeResponse(
      id = assignmentType.id.value,
      name = assignmentType.name.value
    )
