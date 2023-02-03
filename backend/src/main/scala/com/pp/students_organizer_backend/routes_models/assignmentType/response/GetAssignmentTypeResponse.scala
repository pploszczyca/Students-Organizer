package com.pp.students_organizer_backend.routes_models.assignmentType.response

import cats.effect.kernel.Concurrent
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

import java.util.UUID

case class GetAssignmentTypeResponse(
    id: UUID,
    name: String
)
