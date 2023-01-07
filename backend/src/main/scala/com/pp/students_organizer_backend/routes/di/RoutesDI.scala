package com.pp.students_organizer_backend.routes.di

import cats.effect.{Async, Resource}
import com.pp.students_organizer_backend.routes.assignmentType.AssignmentTypeRoutes
import com.pp.students_organizer_backend.use_cases.di.UseCaseDI
import skunk.Session

object RoutesDI {
  def assignmentTypeRoutes[F[_] : Async](databaseResource: Resource[F, Session[F]]): AssignmentTypeRoutes[F] =
    AssignmentTypeRoutes[F](
      getAssignmentTypes = UseCaseDI.assignmentType.getAssignmentTypes(
        databaseResource = databaseResource,
      )
    )
}
