package com.pp.students_organizer_backend.gateways.assignment

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.pp.students_organizer_backend.domain.*
import com.pp.students_organizer_backend.domain.errors.{AssignmentNotFoundException, SubjectNotFoundException, ValidationError, ValidationException}
import com.pp.students_organizer_backend.gateways.assignment.mappers.{AssignmentEntityMapper, GetAssignmentsResponseMapper, GetSingleAssignmentResponseMapper}
import com.pp.students_organizer_backend.routes_models.assignment.request.{InsertAssignmentRequest, UpdateAssignmentRequest}
import com.pp.students_organizer_backend.routes_models.assignment.response.{GetAssignmentsResponse, GetSingleAssignmentResponse}
import com.pp.students_organizer_backend.services.{AssignmentService, MaterialService, SubjectService, TaskService}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{inOrder, verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar.mock

import java.util.UUID

class AssignmentGatewayTest extends AnyFlatSpec:
  private val assignmentService: AssignmentService[IO] = mock
  private val taskService: TaskService[IO] = mock
  private val materialService: MaterialService[IO] = mock
  private val subjectService: SubjectService[IO] = mock
  private given mockAssignmentEntityMapper: AssignmentEntityMapper = mock
  private given mockGetAssignmentsResponseMapper: GetAssignmentsResponseMapper =
    mock
  private given mockGetSingleAssignmentResponseMapper
      : GetSingleAssignmentResponseMapper = mock

  "ON getAllBy" should "get all assignments that belongs to student" in {
    val studentId = mock[StudentId]
    val assignment = mock[AssignmentEntity]
    val response = mock[GetAssignmentsResponse]
    val expected = List(response)

    given getAssignmentsResponseMapper: GetAssignmentsResponseMapper = mock

    when(assignmentService.getAllBy(any())) thenReturn IO(List(assignment))
    when(getAssignmentsResponseMapper.map(any())) thenReturn response

    val actual = tested(
      assignmentService = assignmentService
    ).getAllBy(studentId).unsafeRunSync()

    val inOrderCheck = inOrder(assignmentService, getAssignmentsResponseMapper)
    inOrderCheck.verify(assignmentService).getAllBy(studentId)
    inOrderCheck.verify(getAssignmentsResponseMapper).map(assignment)
    assert(actual == expected)
  }

  "ON getBy" should "get assignment that belongs to student" in {
    val assignmentUUID = mock[UUID]
    val studentId = mock[StudentId]
    val assignmentId = AssignmentId(assignmentUUID)
    val task = mock[TaskEntity]
    val material = mock[MaterialEntity]
    val assignment = mock[AssignmentEntity]
    val expectedResponse = mock[GetSingleAssignmentResponse]

    given getSingleAssignmentResponseMapper: GetSingleAssignmentResponseMapper =
      mock

    when(assignmentService.get(any(), any())) thenReturn IO(Some(assignment))
    when(taskService.getAllBy(any())) thenReturn IO(List(task))
    when(materialService.getAllBy(any())) thenReturn IO(List(material))
    when(
      getSingleAssignmentResponseMapper.map(any(), any(), any())
    ) thenReturn expectedResponse

    val actual = tested(
      assignmentService = assignmentService,
      taskService = taskService,
      materialService = materialService
    ).getBy(assignmentUUID, studentId).unsafeRunSync()

    val inOrderCheck = inOrder(assignmentService,
                               taskService,
                               materialService,
                               getSingleAssignmentResponseMapper
    )
    inOrderCheck.verify(assignmentService).get(assignmentId, studentId)
    inOrderCheck.verify(taskService).getAllBy(assignmentId)
    inOrderCheck.verify(materialService).getAllBy(assignmentId)
    assert(actual == expectedResponse)
  }

  "ON getBy" should "throw error WHEN assignment is not found" in {
    val assignmentUUID = mock[UUID]
    val studentId = mock[StudentId]
    val assignmentId = AssignmentId(assignmentUUID)
    val expectedException = AssignmentNotFoundException(assignmentId)

    when(assignmentService.get(any(), any())) thenReturn IO(None)
    when(taskService.getAllBy(any())) thenReturn IO(List(mock[TaskEntity]))
    when(materialService.getAllBy(any())) thenReturn IO(
      List(mock[MaterialEntity])
    )

    val actualException = intercept[Exception] {
      tested(
        assignmentService = assignmentService,
        taskService = taskService,
        materialService = materialService
      ).getBy(assignmentUUID, studentId).unsafeRunSync()
    }

    val inOrderCheck = inOrder(assignmentService, taskService, materialService)
    inOrderCheck.verify(assignmentService).get(assignmentId, studentId)
    inOrderCheck.verify(taskService).getAllBy(assignmentId)
    inOrderCheck.verify(materialService).getAllBy(assignmentId)
    assert(actualException == expectedException)
  }

  "ON insert" should "add new assignment" in {
    val studentId = mock[StudentId]
    val request = mock[InsertAssignmentRequest]
    val subjectId = mock[SubjectId]
    val subject = mock[SubjectEntity]
    val assignment = fakeAssignment(
      subjectId = subjectId
    )

    given assignmentEntityMapper: AssignmentEntityMapper = mock

    when(
      assignmentEntityMapper.map(any[InsertAssignmentRequest]())
    ) thenReturn Right(assignment)
    when(subjectService.getBy(any(), any())) thenReturn IO(Some(subject))
    when(assignmentService.insert(any())) thenReturn IO.unit

    tested(
      assignmentService = assignmentService,
      subjectService = subjectService,
    ).insert(request, studentId).unsafeRunSync()

    val inOrderCheck = inOrder(assignmentEntityMapper, subjectService, assignmentService)
    inOrderCheck.verify(assignmentEntityMapper).map(request)
    inOrderCheck.verify(subjectService).getBy(subjectId, studentId)
    inOrderCheck.verify(assignmentService).insert(assignment)
  }

  "ON insert" should "throw validation exception WHEN mapper return error" in {
    val studentId = mock[StudentId]
    val request = mock[InsertAssignmentRequest]
    val errorMessage = "errorMessage"
    val error = ValidationError(errorMessage)
    val expectedException = ValidationException(errorMessage)

    given assignmentEntityMapper: AssignmentEntityMapper = mock

    when(
      assignmentEntityMapper.map(any[InsertAssignmentRequest]())
    ) thenReturn Left(error)

    val actualException = intercept[ValidationException] {
      tested().insert(request, studentId).unsafeRunSync()
    }

    verify(assignmentEntityMapper).map(request)
    assert(actualException == expectedException)
  }

  "ON insert" should "throw subject not found exception WHEN subject isn't found" in {
    val studentId = mock[StudentId]
    val request = mock[InsertAssignmentRequest]
    val subjectId = mock[SubjectId]
    val assignment = fakeAssignment(
      subjectId = subjectId
    )
    val expectedException = SubjectNotFoundException(subjectId)

    given assignmentEntityMapper: AssignmentEntityMapper = mock

    when(
      assignmentEntityMapper.map(any[InsertAssignmentRequest]())
    ) thenReturn Right(assignment)
    when(subjectService.getBy(any(), any())) thenReturn IO(None)

    val actualException = intercept[SubjectNotFoundException] {
      tested(
        subjectService = subjectService,
      ).insert(request, studentId).unsafeRunSync()
    }

    val inOrderCheck = inOrder(assignmentEntityMapper, subjectService)
    inOrderCheck.verify(assignmentEntityMapper).map(request)
    inOrderCheck.verify(subjectService).getBy(subjectId, studentId)
    assert(expectedException == actualException)
  }

  "ON update" should "update assignment" in {
    val studentId = mock[StudentId]
    val request = mock[UpdateAssignmentRequest]
    val subjectId = mock[SubjectId]
    val assignment = fakeAssignment(
      subjectId = subjectId
    )
    val subject = mock[SubjectEntity]

    given assignmentEntityMapper: AssignmentEntityMapper = mock

    when(
      assignmentEntityMapper.map(any[UpdateAssignmentRequest]())
    ) thenReturn Right(assignment)
    when(subjectService.getBy(any(), any())) thenReturn IO(Some(subject))
    when(assignmentService.update(any())) thenReturn IO.unit

    tested(
      assignmentService = assignmentService,
      subjectService = subjectService,
    ).update(request, studentId).unsafeRunSync()

    val inOrderCheck = inOrder(assignmentEntityMapper, subjectService, assignmentService)
    inOrderCheck.verify(assignmentEntityMapper).map(request)
    inOrderCheck.verify(subjectService).getBy(subjectId, studentId)
    inOrderCheck.verify(assignmentService).update(assignment)
  }

  "ON update" should "throw validation exception WHEN mapper return error" in {
    val studentId = mock[StudentId]
    val request = mock[UpdateAssignmentRequest]
    val errorMessage = "errorMessage"
    val error = ValidationError(errorMessage)
    val expectedException = ValidationException(errorMessage)

    given assignmentEntityMapper: AssignmentEntityMapper = mock

    when(
      assignmentEntityMapper.map(any[UpdateAssignmentRequest]())
    ) thenReturn Left(error)

    val actualException = intercept[ValidationException] {
      tested().update(request, studentId).unsafeRunSync()
    }

    verify(assignmentEntityMapper).map(request)
    assert(actualException == expectedException)
  }

  "ON update" should "throw subject not found exception WHEN subject isn't found" in {
    val studentId = mock[StudentId]
    val request = mock[UpdateAssignmentRequest]
    val subjectId = mock[SubjectId]
    val assignment = fakeAssignment(
      subjectId = subjectId
    )
    val expectedException = SubjectNotFoundException(subjectId)

    given assignmentEntityMapper: AssignmentEntityMapper = mock

    when(
      assignmentEntityMapper.map(any[UpdateAssignmentRequest]())
    ) thenReturn Right(assignment)
    when(subjectService.getBy(any(), any())) thenReturn IO(None)

    val actualException = intercept[SubjectNotFoundException] {
      tested(
        subjectService = subjectService
      ).update(request, studentId).unsafeRunSync()
    }

    val inOrderCheck = inOrder(assignmentEntityMapper, subjectService)
    inOrderCheck.verify(assignmentEntityMapper).map(request)
    inOrderCheck.verify(subjectService).getBy(subjectId, studentId)
    assert(expectedException == actualException)
  }

  "ON remove" should "remove assignment" in {
    val assignmentUUID = mock[UUID]
    val assignmentId = AssignmentId(assignmentUUID)
    val studentId = mock[StudentId]

    when(assignmentService.remove(any() ,any())) thenReturn IO.unit

    tested(
      assignmentService = assignmentService
    ).remove(assignmentUUID, studentId).unsafeRunSync()

    verify(assignmentService).remove(assignmentId, studentId)
  }

  private def fakeAssignment(
      id: AssignmentId = mock,
      name: AssignmentName = mock,
      description: AssignmentDescription = mock,
      assignmentTypeId: AssignmentTypeId = mock,
      status: AssignmentStatus = mock,
      endDate: AssignmentEndDate = mock,
      subjectId: SubjectId = mock
  ) = AssignmentEntity(
    id = id,
    name = name,
    description = description,
    assignmentTypeId = assignmentTypeId,
    status = status,
    endDate = endDate,
    subjectId = subjectId
  )

  private def tested(
      assignmentService: AssignmentService[IO] = mock,
      taskService: TaskService[IO] = mock,
      materialService: MaterialService[IO] = mock,
      subjectService: SubjectService[IO] = mock
  )(using
      assignmentEntityMapper: AssignmentEntityMapper,
      getAssignmentsResponseMapper: GetAssignmentsResponseMapper,
      getSingleAssignmentResponseMapper: GetSingleAssignmentResponseMapper
  ): AssignmentGateway[IO] =
    AssignmentGateway.make(
      assignmentService = assignmentService,
      taskService = taskService,
      materialService = materialService,
      subjectService = subjectService
    )
