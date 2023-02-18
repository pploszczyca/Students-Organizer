package com.pp.students_organizer_backend.gateways

import cats.effect.Async
import com.pp.students_organizer_backend.gateways.assignment.AssignmentGateway
import com.pp.students_organizer_backend.gateways.assignmentType.AssignmentTypeGateway
import com.pp.students_organizer_backend.gateways.assignmentType.mappers.{
  AssignmentTypeEntityMapper,
  GetAssignmentTypeResponseMapper
}
import com.pp.students_organizer_backend.gateways.material.MaterialGateway
import com.pp.students_organizer_backend.gateways.material.mappers.{
  GetMaterialResponseMapper,
  MaterialEntityMapper
}
import com.pp.students_organizer_backend.gateways.task.TaskGateway
import com.pp.students_organizer_backend.services.Services

object Gateways:
  def make[F[_]: Async](services: Services[F]): Gateways[F] =
    Gateways(services)

class Gateways[F[_]: Async](services: Services[F]):
  lazy val assignmentTypeRoutes: AssignmentTypeGateway[F] =
    AssignmentTypeGateway.make[F](
      assignmentTypeService = services.assignmentType
    )

  lazy val materialRoutes: MaterialGateway[F] =
    MaterialGateway.make[F](
      materialService = services.material
    )

  lazy val taskRoutes: TaskGateway[F] =
    TaskGateway.make[F](
      taskService = services.task
    )

  lazy val assignmentRoutes: AssignmentGateway[F] =
    AssignmentGateway.make[F](
      assignmentService = services.assignment,
      taskService = services.task,
      materialService = services.material
    )
