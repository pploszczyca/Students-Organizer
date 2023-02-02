package com.pp.students_organizer_backend.gateways

import cats.effect.Async
import com.pp.students_organizer_backend.gateways.assignmentType.AssignmentTypeRoutesGateway
import com.pp.students_organizer_backend.gateways.assignmentType.mappers.{AssignmentTypeEntityMapper, GetAssignmentTypeResponseMapper}
import com.pp.students_organizer_backend.gateways.material.MaterialRoutesGateway
import com.pp.students_organizer_backend.gateways.material.mappers.{GetMaterialResponseMapper, MaterialEntityMapper}
import com.pp.students_organizer_backend.services.Services

object Gateways:
  def make[F[_]: Async](services: Services[F]): Gateways[F] =
    Gateways(services)

class Gateways[F[_]: Async](services: Services[F]):
  lazy val assignmentTypeRoutes: AssignmentTypeRoutesGateway[F] =
    AssignmentTypeRoutesGateway.make[F](
      assignmentTypeService = services.assignmentType,
      mapToGetAssignmentTypeResponse = GetAssignmentTypeResponseMapper.map,
      mapToAssignmentType = AssignmentTypeEntityMapper.map
    )

  lazy val materialRoutes: MaterialRoutesGateway[F] =
    MaterialRoutesGateway.make[F](
      materialService = services.material,
      mapToGetMaterialResponse = GetMaterialResponseMapper.map,
      mapToMaterial = MaterialEntityMapper.map
    )
