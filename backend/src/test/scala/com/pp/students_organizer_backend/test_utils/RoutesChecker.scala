package com.pp.students_organizer_backend.test_utils

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import io.circe.Encoder.AsObject.importedAsObjectEncoder
import io.circe.Json
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.{Response, Status}
import org.scalatest.Assertions

object RoutesChecker extends Assertions:
  def check(expectedStatus: Status, expectedResponse: Json, actualResponse: IO[Response[IO]]): Unit = {
    val response = actualResponse.unsafeRunSync()
    checkStatus(response, expectedStatus)
    checkBodyResponse(response, expectedResponse)
  }
  private def checkStatus(response: Response[IO], expectedStatus: Status): Unit = {
    assert(response.status == expectedStatus)
  }

  private def checkBodyResponse(response: Response[IO], expectedResponse: Json): Unit = {
    assert(response.as[Json].unsafeRunSync() == expectedResponse)
  }
