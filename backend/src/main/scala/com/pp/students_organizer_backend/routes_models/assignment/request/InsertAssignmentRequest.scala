package com.pp.students_organizer_backend.routes_models.assignment.request

import java.time.LocalDateTime
import java.util.UUID

case class InsertAssignmentRequest(
    name: String,
    description: String,
    assignmentTypeId: UUID,
    status: String,
    endDate: LocalDateTime,
    subjectId: UUID
)
