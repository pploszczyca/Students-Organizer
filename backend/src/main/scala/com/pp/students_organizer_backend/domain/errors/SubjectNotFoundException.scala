package com.pp.students_organizer_backend.domain.errors

import com.pp.students_organizer_backend.domain.SubjectId

case class SubjectNotFoundException(private val subjectId: SubjectId)
    extends Exception:
  override def getMessage: String =
    s"Subject with id: ${subjectId.value} not found"
