package com.pp.students_organizer_backend.domain

import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.utils.validators.ValidateEmptyString.*

import java.time.LocalDateTime
import java.util.UUID

case class AssignmentEntity(
    id: AssignmentId,
    name: AssignmentName,
    description: AssignmentDescription,
    assignmentTypeId: AssignmentTypeId,
    status: AssignmentStatus,
    endDate: AssignmentEndDate,
    subjectId: SubjectId
)

object AssignmentEntity:
  def create(
      name: String,
      description: String,
      assignmentTypeId: AssignmentTypeId,
      status: String,
      endDate: LocalDateTime,
      subjectId: SubjectId
  ): Either[ValidationError, AssignmentEntity] =
    for
      assignmentId <- AssignmentId.create
      assignment <- create(
        assignmentId = assignmentId,
        name = name,
        description = description,
        assignmentTypeId = assignmentTypeId,
        status = status,
        endDate = endDate,
        subjectId = subjectId
      )
    yield assignment

  def create(
      assignmentUUID: UUID,
      name: String,
      description: String,
      assignmentTypeId: AssignmentTypeId,
      status: String,
      endDate: LocalDateTime,
      subjectId: SubjectId
  ): Either[ValidationError, AssignmentEntity] =
    create(
      assignmentId = AssignmentId(assignmentUUID),
      name = name,
      description = description,
      assignmentTypeId = assignmentTypeId,
      status = status,
      endDate = endDate,
      subjectId = subjectId
    )

  private def create(
      assignmentId: AssignmentId,
      name: String,
      description: String,
      assignmentTypeId: AssignmentTypeId,
      status: String,
      endDate: LocalDateTime,
      subjectId: SubjectId
  ): Either[ValidationError, AssignmentEntity] =
    for
      assignmentName <- AssignmentName.create(name)
      assignmentDescription <- AssignmentDescription.create(description)
      assignmentStatus = AssignmentStatus.valueOf(status)
      assignmentEndDateTimestamp <- AssignmentEndDate.crate(endDate)
    yield AssignmentEntity(
      id = assignmentId,
      name = assignmentName,
      description = assignmentDescription,
      assignmentTypeId = assignmentTypeId,
      status = assignmentStatus,
      endDate = assignmentEndDateTimestamp,
      subjectId = subjectId
    )

case class AssignmentId(value: UUID)
object AssignmentId:
  def create: Either[ValidationError, AssignmentId] =
    Right(AssignmentId(UUID.randomUUID()))

  def create(value: String): Either[ValidationError, AssignmentId] =
    Right(AssignmentId(UUID.fromString(value)))

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

case class AssignmentEndDate(value: LocalDateTime)
object AssignmentEndDate:
  def crate(
      value: LocalDateTime
  ): Either[ValidationError, AssignmentEndDate] =
    Right(AssignmentEndDate(value))
