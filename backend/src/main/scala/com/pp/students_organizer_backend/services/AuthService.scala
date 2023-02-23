package com.pp.students_organizer_backend.services

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.syntax.all.*
import com.pp.students_organizer_backend.domain.{StudentEntity, StudentName, TokenExpiration}
import dev.profunktor.auth.jwt.JwtToken
import dev.profunktor.redis4cats.RedisCommands
import io.circe
import io.circe.*
import io.circe.Encoder.encodeString
import io.circe.generic.auto.*
import io.circe.syntax.*
import pdi.jwt.JwtClaim

import scala.concurrent.duration.{FiniteDuration, MINUTES}

trait AuthService[F[_]]:
  def findStudentBy(token: JwtToken)(claim: JwtClaim): F[Option[StudentEntity]]
  def findTokenBy(name: StudentName): F[Option[JwtToken]]
  def insertStudentWithToken(student: StudentEntity, token: JwtToken): F[Unit]
  def delete(student: StudentEntity, token: JwtToken): F[Unit]

object AuthService:
  def make[F[_]: Concurrent](
      redis: Resource[F, RedisCommands[F, String, String]],
      tokenExpiration: TokenExpiration
  ): AuthService[F] =
    new AuthService[F]:
      override def findStudentBy(token: JwtToken)(claim: JwtClaim): F[Option[StudentEntity]] =
        redis.use {
          _.get(token.value)
            .map {
              _.flatMap { student =>
                circe.jawn.decode[StudentEntity](student).toOption
              }
            }
        }

      override def findTokenBy(name: StudentName): F[Option[JwtToken]] =
        redis.use {
          _.get(name.value)
            .map(_.map(JwtToken.apply))
        }

      override def insertStudentWithToken(
          student: StudentEntity,
          token: JwtToken
      ): F[Unit] =
        redis.use { command =>
          command.setEx(
            token.value,
            student.asJson.noSpaces,
            tokenExpiration.value
          ) *>
            command.setEx(student.name.value, token.value, tokenExpiration.value)
        }

      override def delete(
          student: StudentEntity,
          token: JwtToken
      ): F[Unit] =
        redis.use { command =>
          command.del(token.value) *>
            command.del(student.name.value).void
        }
