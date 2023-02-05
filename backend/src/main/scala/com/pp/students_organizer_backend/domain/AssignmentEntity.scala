package com.pp.students_organizer_backend.domain

import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.utils.validators.ValidateEmptyString.*
import com.pp.students_organizer_backend.utils.validators.ValidateTimestamp.*

import java.util.UUID

case class AssignmentEntity(
    id: AssignmentId,
    name: AssignmentName,
    description: AssignmentDescription,
    assignmentType: AssignmentTypeEntity,
    status: AssignmentStatus,
    endDateTimestamp: AssignmentEndDateTimestamp,
    tasks: List[TaskEntity] = List.empty,
    materials: List[MaterialEntity] = List.empty
)

object AssignmentEntity:
  def create(
              name: String,
              description: String,
              assignmentType: AssignmentTypeEntity,
              status: String,
              endDateTimestamp: Long,
              tasks: List[TaskEntity] = List.empty,
              materials: List[MaterialEntity] = List.empty
            ): Either[ValidationError, AssignmentEntity] =
    for
      assignmentId <- AssignmentId.create
      assignmentName <- AssignmentName.create(name)
      assignmentDescription <- AssignmentDescription.create(description)
      assignmentStatus = AssignmentStatus.valueOf(status)
      assignmentEndDateTimestamp <- AssignmentEndDateTimestamp.crate(endDateTimestamp)
    yield AssignmentEntity(
      id = assignmentId,
      name = assignmentName,
      description = assignmentDescription,
      assignmentType = assignmentType,
      status = assignmentStatus,
      endDateTimestamp = assignmentEndDateTimestamp,
      tasks = tasks,
      materials = materials,
    )

case class AssignmentId(value: UUID)
object AssignmentId:
  def create: Either[ValidationError, AssignmentId] =
    Right(AssignmentId(UUID.randomUUID()))

case class AssignmentName(value: String)
object AssignmentName:
  def create(value: String): Either[ValidationError, AssignmentName] =
    value
      .validateEmptyString("Assignment name can't be empty")
      .map(AssignmentName.apply)

case class AssignmentDescription(value: String)
object AssignmentDescription:
  def create(value: String): Either[ValidationError, AssignmentDescription] =
    value
      .validateEmptyString("Assignment description can't be empty")
      .map(AssignmentDescription.apply)

enum AssignmentStatus:
  case TO_DO, IN_PROGRESS, DONE

case class AssignmentEndDateTimestamp(value: Long)
object AssignmentEndDateTimestamp:
  def crate(value: Long): Either[ValidationError, AssignmentEndDateTimestamp] =
    value
      .validateTimestamp("Assignment end date timestamp can't be empty")
      .map(AssignmentEndDateTimestamp.apply)
