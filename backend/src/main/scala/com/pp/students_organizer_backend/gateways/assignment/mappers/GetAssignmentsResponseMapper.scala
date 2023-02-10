package com.pp.students_organizer_backend.gateways.assignment.mappers

import com.pp.students_organizer_backend.domain.AssignmentEntity
import com.pp.students_organizer_backend.routes_models.assignment.response.GetAssignmentsResponse

trait GetAssignmentsResponseMapper:
  def map(assignmentEntity: AssignmentEntity): GetAssignmentsResponse

object GetAssignmentsResponseMapper:
  given GetAssignmentsResponseMapper with {
    override def map(
        assignmentEntity: AssignmentEntity
    ): GetAssignmentsResponse =
      GetAssignmentsResponse(
        id = assignmentEntity.id.value,
        name = assignmentEntity.name.value,
        assignmentTypeId = assignmentEntity.assignmentTypeId.value,
        status = assignmentEntity.status.toString,
        endDate = assignmentEntity.endDate.value,
        subjectId = assignmentEntity.subjectId.value
      )
  }
