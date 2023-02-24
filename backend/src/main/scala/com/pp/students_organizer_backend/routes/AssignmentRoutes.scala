package com.pp.students_organizer_backend.routes

import cats.effect.Sync
import cats.implicits.catsSyntaxApply
import cats.syntax.all.{catsSyntaxApplicativeError, toFlatMapOps}
import com.pp.students_organizer_backend.domain.StudentEntity
import com.pp.students_organizer_backend.domain.errors.{
  AssignmentNotFoundException,
  SubjectNotFoundException,
  ValidationException
}
import com.pp.students_organizer_backend.gateways.assignment.AssignmentGateway
import com.pp.students_organizer_backend.routes_models.assignment.request.{
  InsertAssignmentRequest,
  UpdateAssignmentRequest
}
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.circe.{JsonDecoder, jsonEncoder, toMessageSyntax}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes}

class AssignmentRoutes[F[_]: JsonDecoder: Sync](
    private val gateway: AssignmentGateway[F]
) extends (AuthMiddleware[F, StudentEntity] => HttpRoutes[F]),
      Http4sDsl[F]:
  private val MAIN_ROUTE_PATH = "assignment"

  private lazy val routes: AuthedRoutes[StudentEntity, F] =
    AuthedRoutes.of {
      case GET -> Root as student =>
        gateway
          .getAllBy(student.id)
          .flatMap { responses => Ok(responses.asJson) }

      case GET -> Root / UUIDVar(assignmentUUID) as student =>
        gateway
          .getBy(assignmentUUID, student.id)
          .flatMap { response => Ok(response.asJson) }
          .handleErrorWith { case AssignmentNotFoundException => NoContent() }

      case request @ POST -> Root as student =>
        request.req
          .asJsonDecode[InsertAssignmentRequest]
          .flatMap { request =>
            gateway.insert(request, student.id) *> Created()
          }
          .handleErrorWith {
            case ValidationException(value) => BadRequest(value.asJson)
            case exception: SubjectNotFoundException =>
              BadRequest(exception.getMessage)
          }

      case request @ PUT -> Root as student =>
        request.req
          .asJsonDecode[UpdateAssignmentRequest]
          .flatMap { request =>
            gateway.update(request, student.id) *> Created()
          }
          .handleErrorWith {
            case ValidationException(value) => BadRequest(value.asJson)
            case exception: SubjectNotFoundException =>
              BadRequest(exception.getMessage)
          }

      case DELETE -> Root / UUIDVar(assignmentUUID) as student =>
        gateway
          .remove(assignmentUUID) *> NoContent()
    }

  override def apply(
      authMiddleware: AuthMiddleware[F, StudentEntity]
  ): HttpRoutes[F] =
    Router(
      MAIN_ROUTE_PATH -> authMiddleware(routes)
    )
