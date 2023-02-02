package com.pp.students_organizer_backend.domain.errors

sealed trait DomainException extends Exception
case class ValidationException(message: String) extends DomainException
