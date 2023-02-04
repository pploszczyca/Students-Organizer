package com.pp.students_organizer_backend.routes

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.errors.ValidationException
import com.pp.students_organizer_backend.gateways.task.TaskRoutesGateway
import com.pp.students_organizer_backend.routes_models.task.request.InsertTaskRequest
import com.pp.students_organizer_backend.routes_models.task.response.GetTaskResponse
import com.pp.students_organizer_backend.test_utils.RoutesChecker
import io.circe.literal.json
import org.http4s.Method.{DELETE, GET, POST}
import org.http4s.Status
import org.http4s.circe.jsonEncoder
import org.http4s.client.dsl.io.*
import org.http4s.implicits.uri
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock

import java.util.UUID

class TaskRoutesTest extends AnyFlatSpec:
  private val gateway: TaskRoutesGateway[IO] = mock

  "GET -> /task" should "return all tasks" in {
    val taskResponse = GetTaskResponse(
      id = UUID.fromString("3efe9e6d-4163-40e5-8ea0-aebe46b502c4"),
      name = "task name",
      isDone = true
    )
    val expectedResponse =
      json"""[{
         "id": "3efe9e6d-4163-40e5-8ea0-aebe46b502c4",
         "name": "task name",
         "isDone": true
      }]"""

    when(gateway.getAll) thenReturn IO(List(taskResponse))

    val actualResponse = tested(gateway = gateway)().orNotFound
      .run(GET(uri"/task"))

    RoutesChecker.check(
      expectedStatus = Status.Ok,
      expectedResponse = expectedResponse,
      actualResponse = actualResponse
    )
  }

  "POST -> /task" should "insert new material" in {
    val jsonRequest =
      json"""{
            "name": "task name",
            "isDone": true
          }"""
    val request = InsertTaskRequest(
      name = "task name",
      isDone = true
    )

    when(gateway.insert(any())) thenReturn IO.unit

    val actualResponse = tested(gateway = gateway)().orNotFound
      .run(POST(jsonRequest, uri"/task"))
      .unsafeRunSync()

    verify(gateway).insert(request)
    RoutesChecker.checkStatus(
      response = actualResponse,
      expectedStatus = Status.Created
    )
  }

  "POST -> /task" should "return error WHEN exception occurs" in {
    val jsonRequest =
      json"""{
            "name": "task name",
            "isDone": true
          }"""
    val request = InsertTaskRequest(
      name = "task name",
      isDone = true
    )
    val errorMessage = "error message"
    val exception = ValidationException(errorMessage)
    val expectedResponse =
      json"""
            "error message"
          """

    when(gateway.insert(any())) thenAnswer (* => throw exception)

    val actualResponse = tested(gateway = gateway)().orNotFound
      .run(POST(jsonRequest, uri"/task"))

    verify(gateway).insert(request)
    RoutesChecker.check(
      expectedStatus = Status.BadRequest,
      expectedResponse = expectedResponse,
      actualResponse = actualResponse
    )
  }

  "DELETE -> /task" should "delete task" in {
    val taskId = UUID.fromString("3efe9e6d-4163-40e5-8ea0-aebe46b502c4")

    when(gateway.remove(any())) thenReturn IO.unit

    val actualResponse = tested(gateway = gateway)().orNotFound
      .run(DELETE(uri"/task/3efe9e6d-4163-40e5-8ea0-aebe46b502c4"))
      .unsafeRunSync()

    verify(gateway).remove(taskId)
    RoutesChecker.checkStatus(
      response = actualResponse,
      expectedStatus = Status.NoContent
    )
  }

  private def tested(
      gateway: TaskRoutesGateway[IO]
  ): TaskRoutes[IO] =
    TaskRoutes(gateway)
