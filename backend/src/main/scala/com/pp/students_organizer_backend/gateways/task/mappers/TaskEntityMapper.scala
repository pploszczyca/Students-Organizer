package com.pp.students_organizer_backend.gateways.task.mappers

import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.domain.{AssignmentId, TaskEntity}
import com.pp.students_organizer_backend.routes_models.task.request.InsertTaskRequest

trait TaskEntityMapper:
  def map(request: InsertTaskRequest): Either[ValidationError, TaskEntity]

object TaskEntityMapper:
  given taskEntityMapper: TaskEntityMapper with {
    override def map(
        request: InsertTaskRequest
    ): Either[ValidationError, TaskEntity] =
      for
        assignmentId <- AssignmentId.create(request.assignmentId)
        task <- TaskEntity.create(
          name = request.name,
          isDone = request.isDone,
          assignmentId = assignmentId
        )
      yield task
  }
