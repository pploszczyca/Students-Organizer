package com.pp.students_organizer_backend.domain

import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.utils.validators.ValidateEmptyString.*

import java.util.UUID

case class SubjectEntity(
    id: SubjectId,
    name: SubjectName,
    termId: TermId
)
object SubjectEntity:
  def create(
      name: String,
      termId: TermId
  ): Either[ValidationError, SubjectEntity] =
    for
      subjectId <- SubjectId.create
      subjectName <- SubjectName.create(name)
    yield SubjectEntity(
      id = subjectId,
      name = subjectName,
      termId = termId
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
