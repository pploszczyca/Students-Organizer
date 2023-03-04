package com.pp.students_organizer_backend.routes

import cats.data.Kleisli
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.{AssignmentId, StudentEntity, StudentId}
import com.pp.students_organizer_backend.domain.errors.{AssignmentNotFoundException, ValidationException}
import com.pp.students_organizer_backend.gateways.task.TaskGateway
import com.pp.students_organizer_backend.routes_models.task.request.InsertTaskRequest
import com.pp.students_organizer_backend.routes_models.task.response.GetTaskResponse
import com.pp.students_organizer_backend.test_utils.{Fakes, RoutesChecker}
import io.circe.literal.json
import org.http4s.Method.{DELETE, GET, POST}
import org.http4s.Status
import org.http4s.circe.jsonEncoder
import org.http4s.client.dsl.io.*
import org.http4s.implicits.uri
import org.http4s.server.AuthMiddleware
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock

import java.util.UUID

class TaskRoutesTest extends AnyFlatSpec:
  private val gateway: TaskGateway[IO] = mock

  "GET -> /task" should "return all tasks" in {
    val studentId = mock[StudentId]
    val student = Fakes.fakeStudent(id = studentId)
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

    when(gateway.getAll(any())) thenReturn IO(List(taskResponse))

    val actualResponse = tested(gateway = gateway)(Fakes.fakeAuthMiddleware(student)).orNotFound
      .run(GET(uri"/task"))

    RoutesChecker.check(
      expectedStatus = Status.Ok,
      expectedResponse = expectedResponse,
      actualResponse = actualResponse
    )
    verify(gateway).getAll(studentId)
  }

  "POST -> /task" should "insert new material" in {
    val studentId = mock[StudentId]
    val student = Fakes.fakeStudent(id = studentId)
    val jsonRequest =
      json"""{
            "name": "task name",
            "isDone": true,
            "assignmentId": "817ec3ac-23eb-421f-a898-8debfbc54b46"
          }"""
    val request = InsertTaskRequest(
      name = "task name",
      isDone = true,
      assignmentId = "817ec3ac-23eb-421f-a898-8debfbc54b46"
    )

    when(gateway.insert(any(), any())) thenReturn IO.unit

    val actualResponse = tested(gateway = gateway)(Fakes.fakeAuthMiddleware(student)).orNotFound
      .run(POST(jsonRequest, uri"/task"))
      .unsafeRunSync()

    verify(gateway).insert(request, studentId)
    RoutesChecker.checkStatus(
      response = actualResponse,
      expectedStatus = Status.Created
    )
  }

  "POST -> /task" should "return bad request WHEN validation exception occurs" in {
    val studentId = mock[StudentId]
    val student = Fakes.fakeStudent(id = studentId)
    val jsonRequest =
      json"""{
            "name": "task name",
            "isDone": true,
            "assignmentId": "817ec3ac-23eb-421f-a898-8debfbc54b46"
          }"""
    val request = InsertTaskRequest(
      name = "task name",
      isDone = true,
      assignmentId = "817ec3ac-23eb-421f-a898-8debfbc54b46"
    )
    val errorMessage = "error message"
    val exception = ValidationException(errorMessage)
    val expectedResponse =
      json"""
            "error message"
          """

    when(gateway.insert(any(), any())) thenAnswer (* => IO.raiseError(exception))

    val actualResponse = tested(gateway = gateway)(Fakes.fakeAuthMiddleware(student)).orNotFound
      .run(POST(jsonRequest, uri"/task"))

    RoutesChecker.check(
      expectedStatus = Status.BadRequest,
      expectedResponse = expectedResponse,
      actualResponse = actualResponse
    )
    verify(gateway).insert(request, studentId)
  }

  "POST -> /task" should "return not found WHEN assignment not found exception occurs" in {
    val studentId = mock[StudentId]
    val student = Fakes.fakeStudent(id = studentId)
    val jsonRequest =
      json"""{
            "name": "task name",
            "isDone": true,
            "assignmentId": "817ec3ac-23eb-421f-a898-8debfbc54b46"
          }"""
    val request = InsertTaskRequest(
      name = "task name",
      isDone = true,
      assignmentId = "817ec3ac-23eb-421f-a898-8debfbc54b46"
    )
    val assigmentUUID = UUID.fromString("817ec3ac-23eb-421f-a898-8debfbc54b46")
    val exception = AssignmentNotFoundException(AssignmentId(assigmentUUID))
    val expectedResponse =
      json"""
            "Assigment with id: 817ec3ac-23eb-421f-a898-8debfbc54b46 not found."
          """

    when(gateway.insert(any(), any())) thenAnswer (* => IO.raiseError(exception))

    val actualResponse = tested(gateway = gateway)(Fakes.fakeAuthMiddleware(student)).orNotFound
      .run(POST(jsonRequest, uri"/task"))

    RoutesChecker.check(
      expectedStatus = Status.NotFound,
      expectedResponse = expectedResponse,
      actualResponse = actualResponse
    )
    verify(gateway).insert(request, studentId)
  }

  "DELETE -> /task" should "delete task" in {
    val studentId = mock[StudentId]
    val student = Fakes.fakeStudent(id = studentId)
    val taskUUID = UUID.fromString("3efe9e6d-4163-40e5-8ea0-aebe46b502c4")

    when(gateway.remove(any(), any())) thenReturn IO.unit

    val actualResponse = tested(gateway = gateway)(Fakes.fakeAuthMiddleware(student)).orNotFound
      .run(DELETE(uri"/task/3efe9e6d-4163-40e5-8ea0-aebe46b502c4"))
      .unsafeRunSync()

    verify(gateway).remove(taskUUID, studentId)
    RoutesChecker.checkStatus(
      response = actualResponse,
      expectedStatus = Status.NoContent
    )
  }

  private def tested(
      gateway: TaskGateway[IO]
  ): TaskRoutes[IO] =
    TaskRoutes(gateway)
