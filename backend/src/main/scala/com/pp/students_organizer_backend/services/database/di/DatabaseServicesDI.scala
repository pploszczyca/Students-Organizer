package com.pp.students_organizer_backend.services.database.di

import cats.effect.kernel.Concurrent
import cats.effect.{IO, Resource}
import com.pp.students_organizer_backend.services.database.AssignmentTypeService
import skunk.Session

object DatabaseServicesDI {
  def assignmentTypeService[F[_] : Concurrent](databaseResource: Resource[F, Session[F]]): AssignmentTypeService[F] =
    AssignmentTypeService[F](databaseResource)
}
