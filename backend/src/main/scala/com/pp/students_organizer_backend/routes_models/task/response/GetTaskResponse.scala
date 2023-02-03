package com.pp.students_organizer_backend.routes_models.task.response

import java.util.UUID

case class GetTaskResponse(
    id: UUID,
    name: String,
    isDone: Boolean
)
