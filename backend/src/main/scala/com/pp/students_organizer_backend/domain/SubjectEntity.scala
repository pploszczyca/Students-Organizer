package com.pp.students_organizer_backend.domain

import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.utils.validators.ValidateEmptyString.*

import java.util.UUID

case class SubjectEntity(
    id: SubjectId,
    name: SubjectName,
    assignments: List[AssignmentEntity]
)
object SubjectEntity:
  def create(
      name: String,
      assignments: List[AssignmentEntity] = List.empty
  ): Either[ValidationError, SubjectEntity] =
    for
      subjectId <- SubjectId.create
      subjectName <- SubjectName.create(name)
    yield SubjectEntity(
      id = subjectId,
      name = subjectName,
      assignments = assignments
    )

case class SubjectId(value: UUID)
object SubjectId:
  def create: Either[ValidationError, SubjectId] =
    Right(SubjectId(UUID.randomUUID()))

case class SubjectName(value: String)
object SubjectName:
  def create(value: String): Either[ValidationError, SubjectName] =
    value
      .validateEmptyString("Subject name can't be empty")
      .map(SubjectName.apply)
