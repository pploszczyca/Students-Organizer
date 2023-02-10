package com.pp.students_organizer_backend.routes_models.assignment.response

import java.time.LocalDateTime
import java.util.UUID

case class GetAssignmentsResponse(
    id: UUID,
    name: String,
    assignmentTypeId: UUID,
    status: String,
    endDate: LocalDateTime,
    subjectId: UUID
)
