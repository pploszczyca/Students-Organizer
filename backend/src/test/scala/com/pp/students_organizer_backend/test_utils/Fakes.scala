package com.pp.students_organizer_backend.test_utils

import cats.data.Kleisli
import cats.effect.IO
import com.pp.students_organizer_backend.domain.{
  StudentEntity,
  StudentId,
  StudentName,
  StudentPassword
}
import org.http4s.server.AuthMiddleware
import org.scalatestplus.mockito.MockitoSugar.mock

object Fakes:
  def fakeAuthMiddleware(
      student: StudentEntity
  ): AuthMiddleware[IO, StudentEntity] =
    AuthMiddleware(Kleisli.pure(student))

  def fakeStudent(
      id: StudentId = mock,
      name: StudentName = mock,
      password: StudentPassword = mock
  ): StudentEntity =
    StudentEntity(
      id = id,
      name = name,
      password = password
    )
