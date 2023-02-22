package com.pp.students_organizer_backend.routes

import cats.effect.Sync
import cats.implicits.catsSyntaxApply
import cats.syntax.all.{catsSyntaxApplicativeError, toFunctorOps}
import cats.syntax.flatMap.toFlatMapOps
import com.pp.students_organizer_backend.gateways.auth.AuthGateway
import com.pp.students_organizer_backend.routes_models.auth.request.{
  LoginRequest,
  RegisterRequest
}
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

import scala.language.postfixOps

class AuthRoutes[F[_]: JsonDecoder: Sync](
    private val gateway: AuthGateway[F]
) extends (() => HttpRoutes[F]),
      Http4sDsl[F]:
  private val MAIN_ROUTE_PATH = "auth"

  private lazy val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ POST -> Root / "register" =>
        request
          .asJsonDecode[RegisterRequest]
          .flatMap(gateway.register)
          .flatMap(response => Ok(response.asJson))

      case request @ POST -> Root / "login" =>
        request
          .asJsonDecode[LoginRequest]
          .flatMap(gateway.login)
          .flatMap(response => Ok(response.asJson))
    }

  override def apply(): HttpRoutes[F] =
    Router(
      MAIN_ROUTE_PATH -> routes
    )
