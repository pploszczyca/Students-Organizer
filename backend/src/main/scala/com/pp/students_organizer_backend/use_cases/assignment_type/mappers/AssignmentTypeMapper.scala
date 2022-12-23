package com.pp.students_organizer_backend.use_cases.assignment_type.mappers

import com.pp.students_organizer_backend.domain.AssignmentTypeEntity
import com.pp.students_organizer_backend.services.database.models.AssignmentTypeModel

class AssignmentTypeMapper {
  def map(assignmentTypeModel: AssignmentTypeModel): AssignmentTypeEntity =
    AssignmentTypeEntity(
      id = assignmentTypeModel.id,
      name = assignmentTypeModel.name,
    )
}
