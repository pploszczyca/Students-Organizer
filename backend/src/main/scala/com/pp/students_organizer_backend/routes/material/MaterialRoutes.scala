package com.pp.students_organizer_backend.routes.material

import cats.effect.Sync
import cats.implicits.catsSyntaxApply
import cats.syntax.all.toFunctorOps
import cats.syntax.flatMap.toFlatMapOps
import com.pp.students_organizer_backend.gateways.material.MaterialRoutesGateway
import com.pp.students_organizer_backend.routes.material.models.request.InsertMaterialRequest
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.HttpRoutes
import org.http4s.circe.{
  JsonDecoder,
  jsonEncoder,
  jsonEncoderOf,
  jsonOf,
  toMessageSyntax
}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

class MaterialRoutes[F[_]: JsonDecoder: Sync](
    private val gateway: MaterialRoutesGateway[F]
) extends (() => HttpRoutes[F]),
      Http4sDsl[F]:
  private val MAIN_ROUTE_PATH = "material"

  private lazy val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        gateway.getAll
          .flatMap { responses => Ok(responses.asJson) }

      case request @ POST -> Root =>
        request
          .asJsonDecode[InsertMaterialRequest]
          .flatMap(gateway.insert) *> Created()

      case DELETE -> Root / IntVar(materialId) =>
        gateway
          .remove(materialId) *> NoContent()
    }

  override def apply(): HttpRoutes[F] =
    Router(
      MAIN_ROUTE_PATH -> routes
    )
