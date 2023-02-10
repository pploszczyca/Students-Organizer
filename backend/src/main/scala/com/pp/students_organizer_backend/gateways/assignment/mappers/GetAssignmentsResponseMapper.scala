package com.pp.students_organizer_backend.gateways.assignment.mappers

import com.pp.students_organizer_backend.domain.AssignmentEntity
import com.pp.students_organizer_backend.routes_models.assignment.response.GetAssignmentsResponse

trait GetAssignmentsResponseMapper:
  def map(assignmentEntity: AssignmentEntity): GetAssignmentsResponse
