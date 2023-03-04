package com.pp.students_organizer_backend.gateways.assignment.mappers

import com.pp.students_organizer_backend.domain.{AssignmentEntity, MaterialEntity, TaskEntity}
import com.pp.students_organizer_backend.routes_models.assignment.response.GetSingleAssignmentResponse

trait GetSingleAssignmentResponseMapper:
  def map(
      assignment: AssignmentEntity,
      tasks: List[TaskEntity],
      materials: List[MaterialEntity]
  ): GetSingleAssignmentResponse

object GetSingleAssignmentResponseMapper:
  given GetSingleAssignmentResponseMapper with {
    override def map(
        assignment: AssignmentEntity,
        tasks: List[TaskEntity],
        materials: List[MaterialEntity]
    ): GetSingleAssignmentResponse =
      GetSingleAssignmentResponse(
        id = assignment.id.value,
        name = assignment.name.value,
        description = assignment.description.value,
        assignmentTypeId = assignment.assignmentTypeId.value,
        status = assignment.status.toString,
        endDate = assignment.endDate.value,
        subjectId = assignment.subjectId.value,
        materials = materials.map(mapMaterial),
        tasks = tasks.map(mapTask)
      )

    private def mapMaterial(materialEntity: MaterialEntity) =
      GetSingleAssignmentResponse.Material(
        id = materialEntity.id.value,
        name = materialEntity.name.value,
        url = materialEntity.url.value
      )

    private def mapTask(taskEntity: TaskEntity) =
      GetSingleAssignmentResponse.Task(
        id = taskEntity.id.value,
        name = taskEntity.name.value,
        isDone = taskEntity.isDone.value
      )
  }
