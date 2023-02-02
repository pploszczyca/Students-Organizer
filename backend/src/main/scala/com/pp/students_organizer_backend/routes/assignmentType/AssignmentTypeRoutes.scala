package com.pp.students_organizer_backend.routes.assignmentType

import cats.effect.kernel.Concurrent
import cats.effect.{Concurrent, Sync}
import cats.implicits.catsSyntaxApply
import cats.syntax.all.toFunctorOps
import cats.syntax.flatMap.toFlatMapOps
import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.gateways.assignmentType.AssignmentTypeRoutesGateway
import com.pp.students_organizer_backend.routes.assignmentType.models.request.InsertAssignmentTypeRequest
import com.pp.students_organizer_backend.routes.assignmentType.models.response.GetAssignmentTypeResponse
import com.pp.students_organizer_backend.services.AssignmentTypeService
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{JsonDecoder, jsonEncoder, jsonEncoderOf, jsonOf, toMessageSyntax}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}

class AssignmentTypeRoutes[F[_]: JsonDecoder: Sync](
    private val gateway: AssignmentTypeRoutesGateway[F]
) extends (() => HttpRoutes[F]),
      Http4sDsl[F]:
  private val MAIN_ROUTE_PATH = "assignmentType"

  private lazy val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        for {
          getAssignmentTypeResponses <- gateway.getAll
          response <- Ok(getAssignmentTypeResponses.asJson)
        } yield response

      case request @ POST -> Root =>
        request
          .asJsonDecode[InsertAssignmentTypeRequest]
          .flatMap(gateway.insert) *> Created()
//          .flatMap {
//            case Left(error) => BadRequest(error.asJson)
//            case Right(_) => Created()
//          }

      case DELETE -> Root / UUIDVar(assignmentTypeId) =>
        gateway
          .remove(assignmentTypeId) *> NoContent()
    }

  override def apply(): HttpRoutes[F] =
    Router(
      MAIN_ROUTE_PATH -> routes
    )
