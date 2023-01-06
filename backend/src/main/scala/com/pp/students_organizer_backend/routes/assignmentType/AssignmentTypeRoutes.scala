package com.pp.students_organizer_backend.routes.assignmentType

import cats.effect.kernel.Concurrent
import cats.effect.{Concurrent, Sync}
import cats.syntax.all.toFunctorOps
import cats.syntax.flatMap.toFlatMapOps
import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.routes.assignmentType.models.GetAssignmentTypeResponse
import com.pp.students_organizer_backend.use_cases.assignment_type.GetAssignmentTypes
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.circe.jsonEncoder

class AssignmentTypeRoutes[F[_] : Sync](private val getAssignmentTypes: GetAssignmentTypes[F]) extends (() => HttpRoutes[F]) {
  private val MAIN_ROUTE_PATH = "assignmentType"

  override def apply(): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root / MAIN_ROUTE_PATH =>
        for {
          assignmentTypes <- getAssignmentTypes()
          response <- Ok(assignmentTypes.map(mapToGetAssignmentTypeResponse).asJson)
        } yield response
    }

  private def mapToGetAssignmentTypeResponse(assignmentType: AssignmentTypeEntity): GetAssignmentTypeResponse =
    GetAssignmentTypeResponse(
      id = assignmentType.id,
      name = assignmentType.name,
    )
}
