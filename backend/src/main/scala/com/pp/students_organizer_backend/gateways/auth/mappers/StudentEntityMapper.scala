package com.pp.students_organizer_backend.gateways.auth.mappers

import com.pp.students_organizer_backend.domain.StudentEntity
import com.pp.students_organizer_backend.domain.errors.{ValidationError, ValidationException}
import com.pp.students_organizer_backend.routes_models.auth.request.RegisterRequest

trait StudentEntityMapper:
  def map(
      request: RegisterRequest
  ): Either[ValidationError, StudentEntity]

object StudentEntityMapper:
  given StudentEntityMapper with {
    override def map(
        request: RegisterRequest
    ): Either[ValidationError, StudentEntity] =
      StudentEntity.create(
        name = request.login,
        password = request.password
      )
  }
