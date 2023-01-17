package com.pp.students_organizer_backend.services

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import skunk.Session

object Services {
  def make[F[_]: Concurrent](database: Resource[F, Session[F]]): Services[F] =
    Services(database)
}

class Services[F[_]: Concurrent](database: Resource[F, Session[F]]):
  lazy val assignmentType: AssignmentTypeService[F] =
    AssignmentTypeService.make(database)
