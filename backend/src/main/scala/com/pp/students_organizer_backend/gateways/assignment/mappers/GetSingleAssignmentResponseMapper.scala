package com.pp.students_organizer_backend.gateways.assignment.mappers

import com.pp.students_organizer_backend.domain.{AssignmentEntity, MaterialEntity, TaskEntity}
import com.pp.students_organizer_backend.routes_models.assignment.response.GetSingleAssignmentResponse

trait GetSingleAssignmentResponseMapper:
  def map(
      assignment: AssignmentEntity,
      tasks: List[TaskEntity],
      materials: List[MaterialEntity]
  ): GetSingleAssignmentResponse
