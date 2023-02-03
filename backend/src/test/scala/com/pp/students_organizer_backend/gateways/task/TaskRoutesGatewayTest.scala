package com.pp.students_organizer_backend.gateways.task

import cats.effect.IO
import com.pp.students_organizer_backend.domain.{TaskEntity, TaskId}
import com.pp.students_organizer_backend.gateways.task.mappers.{GetTaskResponseMapper, TaskEntityMapper}
import com.pp.students_organizer_backend.services.TaskService
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{inOrder, verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.errors.{ValidationError, ValidationException}
import com.pp.students_organizer_backend.routes_models.task.request.InsertTaskRequest
import com.pp.students_organizer_backend.routes_models.task.response.GetTaskResponse

import java.util.UUID

class TaskRoutesGatewayTest extends AnyFlatSpec:
  private val taskService: TaskService[IO] = mock
  private given mockGetTaskResponseMapper: GetTaskResponseMapper = mock
  private given mockTaskEntityMapper: TaskEntityMapper = mock

  "ON getAll" should "return all tasks as response" in {
    val task = mock[TaskEntity]
    val response = mock[GetTaskResponse]
    val expected = List(response)

    given getTaskResponseMapper: GetTaskResponseMapper = mock

    when(getTaskResponseMapper.map(any())) thenReturn response
    when(taskService.getAll) thenReturn IO(List(task))

    val actual = tested(
      taskService = taskService,
    ).getAll.unsafeRunSync()

    verify(getTaskResponseMapper).map(task)
    assert(actual == expected)
  }

  "ON insert" should "insert new task" in {
    val request = mock[InsertTaskRequest]
    val task = mock[TaskEntity]

    given taskEntityMapper: TaskEntityMapper = mock

    when(taskEntityMapper.map(any())) thenReturn Right(task)
    when(taskService.insert(any())) thenReturn IO.unit

    tested(
      taskService = taskService,
    ).insert(request)

    val inOrderCheck = inOrder(taskEntityMapper, taskService)
    inOrderCheck.verify(taskEntityMapper).map(request)
    inOrderCheck.verify(taskService).insert(task)
  }

  "ON insert" should "throw validation exception WHEN mapper return error" in {
    val request = mock[InsertTaskRequest]
    val errorMessage = "errorMessage"
    val error = ValidationError(errorMessage)
    val expectedException = ValidationException(errorMessage)

    given taskEntityMapper: TaskEntityMapper = mock

    when(taskEntityMapper.map(any())) thenReturn Left(error)

    val actualException = intercept[ValidationException] {
      tested().insert(request)
    }

    verify(taskEntityMapper).map(request)
    assert(actualException == expectedException)
  }

  "ON remove" should "remove task" in {
    val id = UUID.fromString("e4ae8cea-dba7-4356-a9d9-f8cc6450e8f7")
    val taskId = TaskId(id)

    when(taskService.remove(any())) thenReturn IO.unit

    tested(
      taskService = taskService
    ).remove(id)

    verify(taskService).remove(taskId)
  }

  private def tested(
      taskService: TaskService[IO] = mock
  )(using
      getTaskResponseMapper: GetTaskResponseMapper,
      taskEntityMapper: TaskEntityMapper
  ): TaskRoutesGateway[IO] =
    TaskRoutesGateway.make(
      taskService = taskService
    )
