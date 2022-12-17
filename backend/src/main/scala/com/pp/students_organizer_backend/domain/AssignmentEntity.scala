package com.pp.students_organizer_backend.domain

case class AssignmentEntity(id: Long,
                            name: String,
                            description: String,
                            assignmentType: AssignmentTypeEntity,
                            isDone: Boolean,
                            endDateTimestamp: Long,
                            tasks: List[TaskEntity] = List.empty,
                            materials: List[MaterialEntity] = List.empty,
                     )
