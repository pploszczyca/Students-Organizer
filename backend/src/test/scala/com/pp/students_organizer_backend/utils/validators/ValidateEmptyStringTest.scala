package com.pp.students_organizer_backend.utils.validators

import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.utils.validators.ValidateEmptyString.*
import org.scalatest.flatspec.AnyFlatSpec

class ValidateEmptyStringTest extends AnyFlatSpec:
  "ON validateEmptyString" should "return validation error WHEN string is empty" in {
    val errorMessage = "error message"
    val emptyString = ""
    val expected = Left(ValidationError(errorMessage))

    val actual = emptyString.validateEmptyString(errorMessage)

    assert(actual == expected)
  }

  "ON validateEmptyString" should "return string WHEN is not empty" in {
    val nonEmptyString = "non empty string"
    val expected = Right(nonEmptyString)

    val actual = nonEmptyString.validateEmptyString()

    assert(actual == expected)
  }
