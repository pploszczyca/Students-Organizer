package com.pp.students_organizer_backend.domain

import com.pp.students_organizer_backend.domain.AssignmentEntity.Status

import java.util.UUID

case class AssignmentEntity(
    id: Long,
    name: String,
    description: String,
    assignmentType: AssignmentTypeEntity,
    status: Status,
    endDateTimestamp: Long,
    tasks: List[TaskEntity] = List.empty,
    materials: List[MaterialEntity] = List.empty
)

object AssignmentEntity:
  enum Status:
    case TO_DO, IN_PROGRESS, DONE
