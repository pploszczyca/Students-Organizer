package com.pp.students_organizer_backend.domain

import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.utils.validators.ValidateEmptyString.*

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
      assignmentTypeId <- AssignmentTypeId.create()
      assignmentTypeName <- AssignmentTypeName.create(name)
    } yield AssignmentTypeEntity(
      id = assignmentTypeId,
      name = assignmentTypeName
    )

case class AssignmentTypeId(value: UUID)
object AssignmentTypeId:
  def create(): Either[ValidationError, AssignmentTypeId] =
    Right(AssignmentTypeId(UUID.randomUUID()))

case class AssignmentTypeName(value: String)
object AssignmentTypeName:
  def create(value: String): Either[ValidationError, AssignmentTypeName] =
    value
      .validateEmptyString("Assignment Type name can't be empty")
      .map(AssignmentTypeName.apply)
