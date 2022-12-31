package com.pp.students_organizer_backend.use_cases.di

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import com.pp.students_organizer_backend.services.database.di.DatabaseServicesDI
import com.pp.students_organizer_backend.use_cases.assignment_type.GetAssignmentTypes
import skunk.Session

object AssignmentTypeDI {
  def getAssignmentTypes[F[_] : Concurrent](databaseResource: Resource[F, Session[F]]): GetAssignmentTypes[F] =
    GetAssignmentTypes[F](
      assignmentTypeService = DatabaseServicesDI.assignmentTypeService[F](
        databaseResource = databaseResource,
      )
    )
}
