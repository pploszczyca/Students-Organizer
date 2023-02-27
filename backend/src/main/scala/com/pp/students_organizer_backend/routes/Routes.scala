package com.pp.students_organizer_backend.routes

import cats.effect.{Async, Resource}
import cats.syntax.all.toSemigroupKOps
import com.pp.students_organizer_backend.domain.StudentEntity
import com.pp.students_organizer_backend.gateways.Gateways
import com.pp.students_organizer_backend.services.{
  AssignmentTypeService,
  Services
}
import dev.profunktor.auth.JwtAuthMiddleware
import dev.profunktor.auth.jwt.{JwtAuth, JwtSymmetricAuth, JwtToken}
import org.http4s.HttpRoutes
import pdi.jwt.{JwtAlgorithm, JwtClaim}
import skunk.Session

object Routes:
  def make[F[_]: Async](
      gateways: Gateways[F],
      authenticate: JwtToken => JwtClaim => F[Option[StudentEntity]]
  ): Routes[F] =
    Routes(gateways, authenticate)

class Routes[F[_]: Async](
    gateways: Gateways[F],
    authenticate: JwtToken => JwtClaim => F[Option[StudentEntity]]
):
  private val jwtSymmetricAuth = JwtAuth.hmac(
    "secretKey",
    JwtAlgorithm.HS256
  )

  private val studentMiddleware =
    JwtAuthMiddleware[F, StudentEntity](jwtSymmetricAuth, authenticate)

  lazy val assignmentType: AssignmentTypeRoutes[F] =
    AssignmentTypeRoutes[F](
      gateway = gateways.assignmentTypeRoutes
    )

  lazy val material: MaterialRoutes[F] =
    MaterialRoutes[F](
      gateway = gateways.materialRoutes
    )

  lazy val task: TaskRoutes[F] =
    TaskRoutes[F](
      gateway = gateways.taskRoutes
    )

  lazy val assignment: AssignmentRoutes[F] =
    AssignmentRoutes[F](
      gateway = gateways.assignmentRoutes
    )

  lazy val auth: AuthRoutes[F] =
    AuthRoutes[F](
      gateway = gateways.auth
    )

  lazy val allRoutes: HttpRoutes[F] =
    assignmentType() <+>
      material(studentMiddleware) <+>
      task(studentMiddleware) <+>
      assignment(studentMiddleware) <+>
      auth()
