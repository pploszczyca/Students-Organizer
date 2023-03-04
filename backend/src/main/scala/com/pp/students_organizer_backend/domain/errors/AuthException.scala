package com.pp.students_organizer_backend.domain.errors

import com.pp.students_organizer_backend.domain.StudentName

sealed trait AuthException extends Exception
case class StudentNotFound(private val name: StudentName) extends AuthException:
  override def getMessage: String = s"Student $name not found."

case class StudentPasswordIsIncorrect(private val name: StudentName)
    extends AuthException:
  override def getMessage: String = s"$name's password is incorrect. Try again."
