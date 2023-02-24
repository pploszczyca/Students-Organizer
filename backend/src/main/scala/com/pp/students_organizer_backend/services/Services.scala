package com.pp.students_organizer_backend.services

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.syntax.all.toFlatMapOps
import com.pp.students_organizer_backend.domain.TokenExpiration
import com.pp.students_organizer_backend.utils.auth.TokenExpirationProvider
import dev.profunktor.redis4cats.RedisCommands
import skunk.Session

import scala.concurrent.duration.{FiniteDuration, MINUTES}

object Services {
  def make[F[_]: Concurrent](
      database: Resource[F, Session[F]],
      redis: Resource[F, RedisCommands[F, String, String]]
  ): Services[F] =
    Services(database, redis)
}

class Services[F[_]: Concurrent](
    database: Resource[F, Session[F]],
    redis: Resource[F, RedisCommands[F, String, String]]
):
  lazy val assignmentType: AssignmentTypeService[F] =
    AssignmentTypeService.make(database)

  lazy val material: MaterialService[F] =
    MaterialService.make(database)

  lazy val task: TaskService[F] =
    TaskService.make(database)

  lazy val assignment: AssignmentService[F] =
    AssignmentService.make(database)
    
  lazy val student: StudentService[F] =
    StudentService.make(database)

  lazy val auth: AuthService[F] =
    AuthService.make(
      redis = redis,
      tokenExpiration = TokenExpiration(FiniteDuration(10, MINUTES)) // TODO: Need to change to TokenExpirationProvider
    )
    
  lazy val subject: SubjectService[F] =
    SubjectService.make(database)
