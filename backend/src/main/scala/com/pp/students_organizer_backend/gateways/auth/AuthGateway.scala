package com.pp.students_organizer_backend.gateways.auth

import cats.effect.kernel.Concurrent
import cats.syntax.all.{toFlatMapOps, toFunctorOps}
import com.pp.students_organizer_backend.domain.{
  StudentEncodedPassword,
  StudentName
}
import com.pp.students_organizer_backend.routes_models.auth.request.{
  LoginRequest,
  RegisterRequest
}
import com.pp.students_organizer_backend.routes_models.auth.response.TokenResponse
import com.pp.students_organizer_backend.services.{AuthService, StudentService}
import com.pp.students_organizer_backend.utils.auth.{PasswordUtils, TokenUtils}
import dev.profunktor.auth.jwt.JwtToken

trait AuthGateway[F[_]]:
  def login(request: LoginRequest): F[TokenResponse]
  def register(request: RegisterRequest): F[TokenResponse]

object AuthGateway:
  def make[F[_]: Concurrent](
      studentService: StudentService[F],
      authService: AuthService[F],
      passwordUtils: PasswordUtils,
      tokenUtils: TokenUtils[F]
  ): AuthGateway[F] =
    new AuthGateway[F]:
      override def login(request: LoginRequest): F[TokenResponse] =
        val studentName = StudentName(request.login)
        val studentEncodedPassword = StudentEncodedPassword(request.password)

        studentService
          .getBy(studentName)
          .flatMap {
            case None => ???
            case Some(student)
                if student.password != passwordUtils.decode(
                  studentEncodedPassword
                ) =>
              ???
            case Some(student) =>
              tokenUtils.createToken
                .map { token =>
                  authService.insertStudentWithToken(student, token)
                  TokenResponse(token.value)
                }
          }

      override def register(request: RegisterRequest): F[TokenResponse] = ???
