package com.pp.students_organizer_backend.routes

import cats.effect.Sync
import cats.syntax.all.{catsSyntaxApplicativeError, catsSyntaxApply}
import cats.syntax.flatMap.toFlatMapOps
import com.pp.students_organizer_backend.domain.StudentEntity
import com.pp.students_organizer_backend.domain.errors.{AssignmentNotFoundException, ValidationError, ValidationException}
import com.pp.students_organizer_backend.gateways.task.TaskGateway
import com.pp.students_organizer_backend.routes_models.task.request.InsertTaskRequest
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.circe.{JsonDecoder, jsonEncoder, toMessageSyntax}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes}

class TaskRoutes[F[_]: JsonDecoder: Sync](
    private val gateway: TaskGateway[F]
) extends (AuthMiddleware[F, StudentEntity] => HttpRoutes[F]),
      Http4sDsl[F]:
  private val MAIN_ROUTE_PATH = "task"

  private lazy val routes: AuthedRoutes[StudentEntity, F] =
    AuthedRoutes.of {
      case GET -> Root as student =>
        gateway.getAll(student.id)
          .flatMap { responses => Ok(responses.asJson) }

      case request @ POST -> Root as student =>
        request
          .req
          .asJsonDecode[InsertTaskRequest]
          .flatMap { request =>
            gateway.insert(request, student.id) *> Created()
          }
          .handleErrorWith {
            case ValidationException(value) => BadRequest(value.asJson)
            case exception: AssignmentNotFoundException => NotFound(exception.getMessage.asJson)
          }

      case DELETE -> Root / UUIDVar(taskId) as student =>
        gateway
          .remove(taskId, student.id) *> NoContent()
    }

  override def apply(
      authMiddleware: AuthMiddleware[F, StudentEntity]
  ): HttpRoutes[F] =
    Router(
      MAIN_ROUTE_PATH -> authMiddleware(routes)
    )
