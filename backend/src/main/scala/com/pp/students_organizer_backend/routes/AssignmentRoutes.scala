package com.pp.students_organizer_backend.routes

import cats.effect.Sync
import cats.implicits.catsSyntaxApply
import cats.syntax.all.{catsSyntaxApplicativeError, toFlatMapOps}
import com.pp.students_organizer_backend.domain.errors.{
  AssignmentNotFoundException,
  ValidationException
}
import com.pp.students_organizer_backend.gateways.assignment.AssignmentRoutesGateway
import com.pp.students_organizer_backend.routes_models.assignment.request.{
  InsertAssignmentRequest,
  UpdateAssignmentRequest
}
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.HttpRoutes
import org.http4s.circe.{JsonDecoder, jsonEncoder, toMessageSyntax}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

class AssignmentRoutes[F[_]: JsonDecoder: Sync](
    private val gateway: AssignmentRoutesGateway[F]
) extends (() => HttpRoutes[F]),
      Http4sDsl[F]:
  private val MAIN_ROUTE_PATH = "assignment"

  private lazy val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / UUIDVar(studentUUID) =>
        gateway
          .getAllBy(studentUUID)
          .flatMap { responses => Ok(responses.asJson) }

      case GET -> Root / UUIDVar(studentUUID) / UUIDVar(assignmentUUID) =>
        gateway
          .getBy(assignmentUUID, studentUUID)
          .flatMap { response => Ok(response.asJson) }
          .handleErrorWith { case AssignmentNotFoundException => NoContent() }

      case request @ POST -> Root =>
        request
          .asJsonDecode[InsertAssignmentRequest]
          .flatMap { request =>
            gateway.insert(request) *> Created()
          }
          .handleErrorWith { case ValidationException(value) =>
            BadRequest(value.asJson)
          }

      case request @ PUT -> Root =>
        request
          .asJsonDecode[UpdateAssignmentRequest]
          .flatMap { request =>
            gateway.update(request) *> Created()
          }
          .handleErrorWith { case ValidationException(value) =>
            BadRequest(value.asJson)
          }

      case DELETE -> Root / UUIDVar(assignmentUUID) =>
        gateway
          .remove(assignmentUUID) *> NoContent()
    }

  override def apply(): HttpRoutes[F] =
    Router(
      MAIN_ROUTE_PATH -> routes
    )
