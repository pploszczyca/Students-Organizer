package com.pp.students_organizer_backend.domain

import com.pp.students_organizer_backend.domain.errors.ValidationError

import java.util.UUID
import scala.util.matching.Regex

case class TermEntity(
    id: TermId,
    number: TermNumber,
    year: TermYear,
    subjects: List[SubjectEntity]
)
object TermEntity:
  def create(
      number: Int,
      year: Int,
      subjects: List[SubjectEntity] = List.empty
  ): Either[ValidationError, TermEntity] =
    for
      termId <- TermId.create
      termNumber <- TermNumber.create(number)
      termYear <- TermYear.create(year)
    yield TermEntity(
      id = termId,
      number = termNumber,
      year = termYear,
      subjects = subjects
    )

case class TermId(value: UUID)
object TermId:
  def create: Either[ValidationError, TermId] =
    Right(TermId(UUID.randomUUID()))

case class TermNumber(value: Int)
object TermNumber:
  def create(value: Int): Either[ValidationError, TermNumber] =
    if (value <= 0) Left(ValidationError("Term Number must be positive"))
    else Right(TermNumber(value))

case class TermYear(value: Int)
object TermYear:
  private val MIN_YEAR = 1000
  private val MAX_YEAR = 2999

  def create(value: Int): Either[ValidationError, TermYear] =
    if (value >= MIN_YEAR && value <= MAX_YEAR) Right(TermYear(value))
    else
      Left(
        ValidationError(
          s"Term Year must be a number greater than $MIN_YEAR and smaller than $MAX_YEAR"
        )
      )
