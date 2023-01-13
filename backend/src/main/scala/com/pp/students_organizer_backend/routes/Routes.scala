package com.pp.students_organizer_backend.routes

import cats.effect.{Async, Resource}
import com.pp.students_organizer_backend.routes.assignmentType.AssignmentTypeRoutes
import com.pp.students_organizer_backend.services.{AssignmentTypeService, Services}
import skunk.Session

object Routes:
  def make[F[_] : Async](services: Services[F]): Routes[F] =
    Routes(services)

class Routes[F[_] : Async](services: Services[F]):
  lazy val assignmentType: AssignmentTypeRoutes[F] =
    AssignmentTypeRoutes[F](
      assignmentTypeService = services.assignmentType,
    )

  lazy val allRoutes: AssignmentTypeRoutes[F] = assignmentType
