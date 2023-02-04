package com.pp.students_organizer_backend.routes

import cats.effect.{Async, Resource}
import cats.syntax.all.toSemigroupKOps
import com.pp.students_organizer_backend.gateways.Gateways
import com.pp.students_organizer_backend.services.{
  AssignmentTypeService,
  Services
}
import org.http4s.HttpRoutes
import skunk.Session

object Routes:
  def make[F[_]: Async](gateways: Gateways[F]): Routes[F] =
    Routes(gateways)

class Routes[F[_]: Async](gateways: Gateways[F]):
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
      gateway = gateways.taskRoues
    )

  lazy val allRoutes: HttpRoutes[F] =
    assignmentType() <+> material() <+> task()
