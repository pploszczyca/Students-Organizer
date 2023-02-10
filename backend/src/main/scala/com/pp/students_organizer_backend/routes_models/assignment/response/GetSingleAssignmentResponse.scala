package com.pp.students_organizer_backend.routes_models.assignment.response

import com.pp.students_organizer_backend.routes_models.assignment.response.GetSingleAssignmentResponse.{
  Material,
  Task
}

import java.time.LocalDateTime
import java.util.UUID

case class GetSingleAssignmentResponse(
    id: UUID,
    name: String,
    description: String,
    assignmentTypeId: UUID,
    status: String,
    endDate: LocalDateTime,
    subjectId: UUID,
    materials: List[Material],
    tasks: List[Task]
)

object GetSingleAssignmentResponse:
  case class Material(
      id: UUID,
      name: String,
      url: String
  )

  case class Task(
      id: UUID,
      name: String,
      isDone: Boolean
  )
