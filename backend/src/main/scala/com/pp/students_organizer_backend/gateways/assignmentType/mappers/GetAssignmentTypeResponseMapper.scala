package com.pp.students_organizer_backend.gateways.assignmentType.mappers

import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.routes_models.assignmentType.response.GetAssignmentTypeResponse

trait GetAssignmentTypeResponseMapper:
  def map(
      assignmentType: AssignmentTypeEntity
  ): GetAssignmentTypeResponse

object GetAssignmentTypeResponseMapper:
  given getAssignmentTypeResponseMapper: GetAssignmentTypeResponseMapper with {
    override def map(
        assignmentType: AssignmentTypeEntity
    ): GetAssignmentTypeResponse =
      GetAssignmentTypeResponse(
        id = assignmentType.id.value,
        name = assignmentType.name.value
      )
  }
