package com.pp.students_organizer_backend.gateways.auth

import cats.MonadThrow
import cats.effect.kernel.Concurrent
import cats.implicits.catsSyntaxApply
import cats.syntax.all.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import com.pp.students_organizer_backend.domain.errors.{StudentNotFound, StudentPasswordIsIncorrect}
import com.pp.students_organizer_backend.domain.{StudentEncodedPassword, StudentEntity, StudentName}
import com.pp.students_organizer_backend.gateways.auth.mappers.StudentEntityMapper
import com.pp.students_organizer_backend.routes_models.auth.request.{LoginRequest, RegisterRequest}
import com.pp.students_organizer_backend.routes_models.auth.response.TokenResponse
import com.pp.students_organizer_backend.services.{AuthService, StudentService}
import com.pp.students_organizer_backend.utils.NonErrorValueMapper.*
import com.pp.students_organizer_backend.utils.auth.{PasswordUtils, TokenUtils}
import dev.profunktor.auth.jwt.JwtToken

trait AuthGateway[F[_]]:
  def login(request: LoginRequest): F[TokenResponse]
  def register(request: RegisterRequest): F[TokenResponse]

object AuthGateway:
  def make[F[_]: Concurrent: MonadThrow](
      studentService: StudentService[F],
      authService: AuthService[F],
      passwordUtils: PasswordUtils,
      tokenUtils: TokenUtils[F]
  )(using
      studentMapper: StudentEntityMapper
  ): AuthGateway[F] =
    new AuthGateway[F]:
      override def login(request: LoginRequest): F[TokenResponse] =
        val studentName = StudentName(request.login)
        val studentEncodedPassword = StudentEncodedPassword(request.password)

        studentService
          .getBy(studentName)
          .flatMap {
            case None =>
              StudentNotFound(studentName).raiseError[F, TokenResponse]
            case Some(student)
                if student.password != passwordUtils.decode(
                  studentEncodedPassword
                ) =>
              StudentPasswordIsIncorrect(studentName)
                .raiseError[F, TokenResponse]
            case Some(student) => createToken(student)
          }

      override def register(request: RegisterRequest): F[TokenResponse] =
        studentMapper
          .map(request)
          .mapWithNoError { student =>
            studentService.insert(student) *> createToken(student)
          }

      private def createToken(student: StudentEntity): F[TokenResponse] =
        tokenUtils.createToken
          .flatMap { token =>
            authService.insertStudentWithToken(student, token) *> TokenResponse(
              token.value
            ).pure[F]
          }
