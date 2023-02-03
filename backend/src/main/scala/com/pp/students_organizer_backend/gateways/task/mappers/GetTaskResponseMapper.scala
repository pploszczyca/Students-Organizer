package com.pp.students_organizer_backend.gateways.task.mappers

import com.pp.students_organizer_backend.domain.TaskEntity
import com.pp.students_organizer_backend.routes.task.models.response.GetTaskResponse

trait GetTaskResponseMapper:
  def map(task: TaskEntity): GetTaskResponse

object GetTaskResponseMapper:
  given getTaskResponseMapper: GetTaskResponseMapper with {
    override def map(
        task: TaskEntity
    ): GetTaskResponse =
      GetTaskResponse(
        id = task.id.value,
        name = task.name.value,
        isDone = task.isDone.value
      )
  }
