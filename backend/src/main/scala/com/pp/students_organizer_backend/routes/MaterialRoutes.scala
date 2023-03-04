package com.pp.students_organizer_backend.routes

import cats.effect.Sync
import cats.implicits.catsSyntaxApply
import cats.syntax.all.{catsSyntaxApplicativeError, toFunctorOps}
import cats.syntax.flatMap.toFlatMapOps
import com.pp.students_organizer_backend.domain.StudentEntity
import com.pp.students_organizer_backend.domain.errors.{AssignmentNotFoundException, ValidationException}
import com.pp.students_organizer_backend.gateways.material.MaterialGateway
import com.pp.students_organizer_backend.routes_models.material.request.InsertMaterialRequest
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.circe.{JsonDecoder, jsonEncoder, jsonEncoderOf, jsonOf, toMessageSyntax}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes}

class MaterialRoutes[F[_]: JsonDecoder: Sync](
    private val gateway: MaterialGateway[F]
) extends (AuthMiddleware[F, StudentEntity] => HttpRoutes[F]),
      Http4sDsl[F]:
  private val MAIN_ROUTE_PATH = "material"

  private lazy val routes: AuthedRoutes[StudentEntity, F] =
    AuthedRoutes.of {
      case GET -> Root as student =>
        gateway
          .getAll(student.id)
          .flatMap { responses => Ok(responses.asJson) }

      case request @ POST -> Root as student =>
        request.req
          .asJsonDecode[InsertMaterialRequest]
          .flatMap { request =>
            gateway.insert(request, student.id) *> Created()
          }
          .handleErrorWith {
            case ValidationException(value) => BadRequest(value.asJson)
            case exception: AssignmentNotFoundException =>
              NotFound(exception.getMessage.asJson)
          }

      case DELETE -> Root / UUIDVar(materialId) as student =>
        gateway
          .remove(materialId, student.id) *> NoContent()
    }

  override def apply(
      authMiddleware: AuthMiddleware[F, StudentEntity]
  ): HttpRoutes[F] =
    Router(
      MAIN_ROUTE_PATH -> authMiddleware(routes)
    )
