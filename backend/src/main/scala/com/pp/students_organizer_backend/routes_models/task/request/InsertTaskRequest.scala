package com.pp.students_organizer_backend.routes_models.task.request

import com.pp.students_organizer_backend.domain.AssignmentId

case class InsertTaskRequest(
    name: String,
    isDone: Boolean,
    assignmentId: String
)
