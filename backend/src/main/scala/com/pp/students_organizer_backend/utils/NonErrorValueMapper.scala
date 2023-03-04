package com.pp.students_organizer_backend.utils

import com.pp.students_organizer_backend.domain.errors.{ValidationError, ValidationException}

object NonErrorValueMapper {
  extension [T](value: Either[ValidationError, T])
    def mapWithNoError[V](f: T => V): V =
      value match
        case Right(value) => f(value)
        case Left(ValidationError(errorMessage)) =>
          throw ValidationException(errorMessage)
}
