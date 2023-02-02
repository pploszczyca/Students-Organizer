package com.pp.students_organizer_backend.domain

import com.pp.students_organizer_backend.domain.common.ValidationError

import java.util.UUID

case class AssignmentTypeEntity(
    id: AssignmentTypeId,
    name: AssignmentTypeName
)

object AssignmentTypeEntity:
  def create(
      name: String
  ): Either[ValidationError, AssignmentTypeEntity] =
    for {
      assignmentTypeid <- AssignmentTypeId.create()
      assignmentTypeName <- AssignmentTypeName.create(name)
    } yield AssignmentTypeEntity(
      id = assignmentTypeid,
      name = assignmentTypeName
    )

case class AssignmentTypeId(value: UUID)
object AssignmentTypeId:
  def create(): Either[ValidationError, AssignmentTypeId] =
    Right(AssignmentTypeId(UUID.randomUUID()))

case class AssignmentTypeName(value: String)
object AssignmentTypeName:
  def create(value: String): Either[ValidationError, AssignmentTypeName] =
    if (value.isEmpty)
      Left(ValidationError("Assignment Type name can't be empty"))
    else Right(AssignmentTypeName(value))
