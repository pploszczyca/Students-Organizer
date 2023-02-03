package com.pp.students_organizer_backend.routes.task.models.request

case class InsertTaskRequest(
    name: String,
    isDone: Boolean
)
