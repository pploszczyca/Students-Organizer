package com.pp.students_organizer_backend.utils.validators

import com.pp.students_organizer_backend.domain.errors.ValidationError

object ValidateTimestamp:
  extension (value: Long)
    def validateTimestamp(
        errorMessage: String = "Wrong timestamp"
    ): Either[ValidationError, Long] =
      if (value < 0) Left(ValidationError(errorMessage))
      else Right(value)
