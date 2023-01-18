package com.pp.students_organizer_backend.gateways

import cats.effect.Async
import com.pp.students_organizer_backend.gateways.assignmentType.AssignmentTypeRoutesGateway
import com.pp.students_organizer_backend.gateways.assignmentType.mappers.{
  AssignmentTypeEntityMapper,
  GetAssignmentTypeResponseMapper
}
import com.pp.students_organizer_backend.services.Services

object Gateways:
  def make[F[_]: Async](services: Services[F]): Gateways[F] =
    Gateways(services)

class Gateways[F[_]: Async](services: Services[F]):
  lazy val assignmentTypeRoutes: AssignmentTypeRoutesGateway[F] =
    AssignmentTypeRoutesGateway.make[F](
      assignmentTypeService = services.assignmentType,
      mapToGetAssignmentTypeResponse = GetAssignmentTypeResponseMapper(),
      mapToAssignmentType = AssignmentTypeEntityMapper(),
    )
