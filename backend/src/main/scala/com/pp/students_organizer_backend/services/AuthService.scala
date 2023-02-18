package com.pp.students_organizer_backend.services

import cats.effect.kernel.Concurrent
import cats.syntax.all.*
import com.pp.students_organizer_backend.domain.{
  StudentEntity,
  StudentName,
  TokenExpiration
}
import dev.profunktor.auth.jwt.JwtToken
import dev.profunktor.redis4cats.RedisCommands
import io.circe
import io.circe.*
import io.circe.Encoder.encodeString
import io.circe.generic.auto.*
import io.circe.syntax.*

import scala.concurrent.duration.{FiniteDuration, MINUTES}

trait AuthService[F[_]]:
  def findStudentBy(token: JwtToken): F[Option[StudentEntity]]
  def findTokenBy(name: StudentName): F[Option[JwtToken]]
  def insertStudentWithToken(student: StudentEntity, token: JwtToken): F[Unit]
  def delete(student: StudentEntity, token: JwtToken): F[Unit]

object AuthService:
  def make[F[_]: Concurrent](
      redis: RedisCommands[F, String, String],
      tokenExpiration: TokenExpiration
  ): AuthService[F] =
    new AuthService[F]:
      override def findStudentBy(token: JwtToken): F[Option[StudentEntity]] =
        redis
          .get(token.value)
          .map {
            _.flatMap { student =>
              circe.jawn.decode[StudentEntity](student).toOption
            }
          }

      override def findTokenBy(name: StudentName): F[Option[JwtToken]] =
        redis
          .get(name.value)
          .map(_.map(JwtToken.apply))

      override def insertStudentWithToken(
          student: StudentEntity,
          token: JwtToken
      ): F[Unit] =
        redis.setEx(
          token.value,
          student.asJson.noSpaces,
          tokenExpiration.value,
        ) *>
          redis.setEx(student.name.value, token.value, tokenExpiration.value)

      override def delete(
          student: StudentEntity,
          token: JwtToken
      ): F[Unit] =
        redis.del(token.value) *>
          redis.del(student.name.value).void
