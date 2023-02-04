package com.pp.students_organizer_backend.routes

import cats.effect.Sync
import cats.syntax.all.{catsSyntaxApplicativeError, catsSyntaxApply}
import cats.syntax.flatMap.toFlatMapOps
import com.pp.students_organizer_backend.domain.errors.{ValidationError, ValidationException}
import com.pp.students_organizer_backend.gateways.task.TaskRoutesGateway
import com.pp.students_organizer_backend.routes_models.task.request.InsertTaskRequest
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.HttpRoutes
import org.http4s.circe.{JsonDecoder, jsonEncoder, toMessageSyntax}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

class TaskRoutes[F[_]: JsonDecoder: Sync](
    private val gateway: TaskRoutesGateway[F]
) extends (() => HttpRoutes[F]),
      Http4sDsl[F]:
  private val MAIN_ROUTE_PATH = "task"

  private lazy val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        gateway.getAll
          .flatMap { responses => Ok(responses.asJson) }

      case request @ POST -> Root =>
        request
          .asJsonDecode[InsertTaskRequest]
          .flatMap { request =>
            gateway.insert(request) *> Created()
          }
          .handleErrorWith { case ValidationException(value) =>
            BadRequest(value.asJson)
          }

      case DELETE -> Root / UUIDVar(taskId) =>
        gateway
          .remove(taskId) *> NoContent()
    }

  override def apply(): HttpRoutes[F] =
    Router(
      MAIN_ROUTE_PATH -> routes
    )
