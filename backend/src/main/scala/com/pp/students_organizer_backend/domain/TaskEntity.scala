package com.pp.students_organizer_backend.domain

import com.pp.students_organizer_backend.domain.errors.ValidationError

import java.util.UUID

case class TaskEntity(
    id: TaskId,
    name: TaskName,
    isDone: TaskIsDone
)

object TaskEntity:
  def create(
      name: String,
      isDone: Boolean
  ): Either[ValidationError, TaskEntity] =
    for {
      taskId <- TaskId.create()
      taskName <- TaskName.create(name)
      taskIsDone <- TaskIsDone.create(isDone)
    } yield TaskEntity(
      id = taskId,
      name = taskName,
      isDone = taskIsDone
    )

case class TaskId(value: UUID)
object TaskId:
  def create(): Either[ValidationError, TaskId] =
    Right(TaskId(UUID.randomUUID()))

case class TaskName(value: String)
object TaskName:
  def create(value: String): Either[ValidationError, TaskName] =
    if (value.isEmpty) Left(ValidationError("Task name can't be empty"))
    else Right(TaskName(value))

case class TaskIsDone(value: Boolean)
object TaskIsDone:
  def create(value: Boolean): Either[ValidationError, TaskIsDone] =
    Right(TaskIsDone(value))
