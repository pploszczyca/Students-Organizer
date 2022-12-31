package com.pp.students_organizer_backend.services.database.models

import com.pp.students_organizer_backend.domain.AssignmentTypeEntity

case class AssignmentTypeModel(id: Int,
                               name: String,
                              )

object AssignmentTypeModel {
  def mapToAssignmentTypeEntity(assignmentTypeModel: AssignmentTypeModel): AssignmentTypeEntity =
    AssignmentTypeEntity(
      id = assignmentTypeModel.id,
      name = assignmentTypeModel.name,
    )
}

