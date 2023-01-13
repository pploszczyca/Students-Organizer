package com.pp.students_organizer_backend.routes.assignmentType.models.response

import cats.effect.kernel.Concurrent
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

case class GetAssignmentTypeResponse(id: Long,
                                     name: String,
                                    )
