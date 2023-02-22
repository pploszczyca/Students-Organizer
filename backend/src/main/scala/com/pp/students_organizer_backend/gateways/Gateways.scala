package com.pp.students_organizer_backend.gateways

import cats.effect.Async
import com.pp.students_organizer_backend.gateways.assignment.AssignmentGateway
import com.pp.students_organizer_backend.gateways.assignmentType.AssignmentTypeGateway
import com.pp.students_organizer_backend.gateways.assignmentType.mappers.{AssignmentTypeEntityMapper, GetAssignmentTypeResponseMapper}
import com.pp.students_organizer_backend.gateways.auth.AuthGateway
import com.pp.students_organizer_backend.gateways.material.MaterialGateway
import com.pp.students_organizer_backend.gateways.material.mappers.{GetMaterialResponseMapper, MaterialEntityMapper}
import com.pp.students_organizer_backend.gateways.task.TaskGateway
import com.pp.students_organizer_backend.services.Services
import com.pp.students_organizer_backend.utils.auth.{PasswordUtils, TokenExpirationProvider, TokenUtils}

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
    
  lazy val auth: AuthGateway[F] =
    AuthGateway.make[F](
      studentService = services.student,
      authService = services.auth,
      passwordUtils = PasswordUtils.make,
      tokenUtils = TokenUtils.make(
        tokenExpirationProvider = TokenExpirationProvider.make
      )
    )
