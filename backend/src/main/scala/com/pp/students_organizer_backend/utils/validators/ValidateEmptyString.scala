package com.pp.students_organizer_backend.utils.validators

import com.pp.students_organizer_backend.domain.errors.ValidationError

object ValidateEmptyString:
  extension (value: String)
    def validateEmptyString(
        errorMessage: String = "Can't be empty"
    ): Either[ValidationError, String] =
      if (value.isEmpty) Left(ValidationError(errorMessage))
      else Right(value)
