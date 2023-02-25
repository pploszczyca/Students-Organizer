package com.pp.students_organizer_backend.domain.errors

import com.pp.students_organizer_backend.domain.AssignmentId

case class AssignmentNotFoundException(private val assigmentId: AssignmentId) extends Exception:
  override def getMessage: String = s"Assigment with id: ${assigmentId.value} not found."
