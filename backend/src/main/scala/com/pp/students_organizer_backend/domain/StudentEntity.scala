package com.pp.students_organizer_backend.domain

import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.utils.validators.ValidateEmptyString.*

import java.util.UUID

case class StudentEntity(
    id: StudentId,
    name: StudentName,
    password: StudentPassword,
)
object StudentEntity:
  def create(
      name: String,
      password: String,
  ): Either[ValidationError, StudentEntity] =
    for
      studentId <- StudentId.create
      studentName <- StudentName.create(name)
      studentPassword <- StudentPassword.create(password)
    yield StudentEntity(
      id = studentId,
      name = studentName,
      password = studentPassword,
    )

case class StudentId(value: UUID)
object StudentId:
  def create: Either[ValidationError, StudentId] =
    Right(StudentId(UUID.randomUUID()))

case class StudentName(value: String)
object StudentName:
  def create(value: String): Either[ValidationError, StudentName] =
    value
      .validateEmptyString("Student Name can't be null")
      .map(StudentName.apply)

case class StudentPassword(value: String)
object StudentPassword:
  def create(value: String): Either[ValidationError, StudentPassword] =
    Right(StudentPassword(value))
    
case class StudentEncodedPassword(value: String)