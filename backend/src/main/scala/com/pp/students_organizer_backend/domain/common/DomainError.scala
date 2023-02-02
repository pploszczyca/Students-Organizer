package com.pp.students_organizer_backend.domain.common

sealed trait DomainError extends Exception
case class BadArgumentsError(message: String) extends DomainError
