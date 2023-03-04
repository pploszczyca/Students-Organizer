package com.pp.students_organizer_backend.gateways.task

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.*
import com.pp.students_organizer_backend.domain.errors.{AssignmentNotFoundException, ValidationError, ValidationException}
import com.pp.students_organizer_backend.gateways.task.mappers.{GetTaskResponseMapper, TaskEntityMapper}
import com.pp.students_organizer_backend.routes_models.task.request.InsertTaskRequest
import com.pp.students_organizer_backend.routes_models.task.response.GetTaskResponse
import com.pp.students_organizer_backend.services.{AssignmentService, TaskService}
import org.mockito.ArgumentMatchers.{any, anySet}
import org.mockito.Mockito.{inOrder, verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock

import java.util.UUID

class TaskGatewayTest extends AnyFlatSpec:
  private val taskService: TaskService[IO] = mock
  private val assignmentService: AssignmentService[IO] = mock
  private given mockGetTaskResponseMapper: GetTaskResponseMapper = mock
  private given mockTaskEntityMapper: TaskEntityMapper = mock

  "ON getAll" should "return all tasks as response" in {
    val studentId = mock[StudentId]
    val task = mock[TaskEntity]
    val response = mock[GetTaskResponse]
    val expected = List(response)

    given getTaskResponseMapper: GetTaskResponseMapper = mock

    when(getTaskResponseMapper.map(any())) thenReturn response
    when(taskService.getAll(any())) thenReturn IO(List(task))

    val actual = tested(
      taskService = taskService
    ).getAll(studentId).unsafeRunSync()

    val inOrderCheck = inOrder(getTaskResponseMapper, taskService)
    inOrderCheck.verify(taskService).getAll(studentId)
    inOrderCheck.verify(getTaskResponseMapper).map(task)
    assert(actual == expected)
  }

  "ON insert" should "throw validation exception WHEN mapper return error" in {
    val studentId = mock[StudentId]
    val request = mock[InsertTaskRequest]
    val errorMessage = "errorMessage"
    val error = ValidationError(errorMessage)
    val expectedException = ValidationException(errorMessage)

    given taskEntityMapper: TaskEntityMapper = mock

    when(taskEntityMapper.map(any())) thenReturn Left(error)

    val actualException = intercept[ValidationException] {
      tested().insert(request, studentId)
    }

    verify(taskEntityMapper).map(request)
    assert(actualException == expectedException)
  }

  "ON insert" should "insert new task" in {
    val studentId = mock[StudentId]
    val request = mock[InsertTaskRequest]
    val assignmentId = mock[AssignmentId]
    val task = fakeTask(
      assignmentId = assignmentId
    )
    val assignment = mock[AssignmentEntity]

    given taskEntityMapper: TaskEntityMapper = mock

    when(taskEntityMapper.map(any())) thenReturn Right(task)
    when(assignmentService.get(any(), any())) thenReturn IO(Some(assignment))
    when(taskService.insert(any())) thenReturn IO.unit

    tested(
      taskService = taskService,
      assignmentService = assignmentService
    ).insert(request, studentId).unsafeRunSync()

    val inOrderCheck = inOrder(taskEntityMapper, assignmentService, taskService)
    inOrderCheck.verify(taskEntityMapper).map(request)
    inOrderCheck.verify(assignmentService).get(assignmentId, studentId)
    inOrderCheck.verify(taskService).insert(task)
  }

  "ON insert" should "throw assigment not found exception WHEN assigment is not found" in {
    val studentId = mock[StudentId]
    val request = mock[InsertTaskRequest]
    val assignmentId = mock[AssignmentId]
    val task = fakeTask(
      assignmentId = assignmentId
    )
    val expectedException = AssignmentNotFoundException(assignmentId)

    given taskEntityMapper: TaskEntityMapper = mock

    when(taskEntityMapper.map(any())) thenReturn Right(task)
    when(assignmentService.get(any(), any())) thenReturn IO(None)

    val actualException = intercept[AssignmentNotFoundException] {
      tested(
        assignmentService = assignmentService
      ).insert(request, studentId).unsafeRunSync()
    }

    val inOrderCheck = inOrder(taskEntityMapper, assignmentService)
    inOrderCheck.verify(taskEntityMapper).map(request)
    inOrderCheck.verify(assignmentService).get(assignmentId, studentId)
    assert(actualException == expectedException)
  }

  "ON remove" should "remove task" in {
    val studentId = mock[StudentId]
    val id = UUID.fromString("e4ae8cea-dba7-4356-a9d9-f8cc6450e8f7")
    val taskId = TaskId(id)

    when(taskService.remove(any(), any())) thenReturn IO.unit

    tested(
      taskService = taskService
    ).remove(id, studentId)

    verify(taskService).remove(taskId, studentId)
  }

  private def fakeTask(
      id: TaskId = mock,
      name: TaskName = mock,
      isDone: TaskIsDone = mock,
      assignmentId: AssignmentId = mock
  ) = TaskEntity(
    id = id,
    name = name,
    isDone = isDone,
    assignmentId = assignmentId
  )

  private def tested(
      taskService: TaskService[IO] = mock,
      assignmentService: AssignmentService[IO] = mock
  )(using
      getTaskResponseMapper: GetTaskResponseMapper,
      taskEntityMapper: TaskEntityMapper
  ): TaskGateway[IO] =
    TaskGateway.make(
      taskService = taskService,
      assignmentService = assignmentService
    )
