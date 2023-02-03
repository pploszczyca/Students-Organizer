package com.pp.students_organizer_backend.routes.task.models.response

import java.util.UUID

case class GetTaskResponse(
    id: UUID,
    name: String,
    isDone: Boolean
)
