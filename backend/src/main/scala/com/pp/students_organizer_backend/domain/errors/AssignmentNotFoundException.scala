package com.pp.students_organizer_backend.domain.errors

import java.util.UUID

case class AssignmentNotFoundException(assigmentId: UUID) extends Exception
