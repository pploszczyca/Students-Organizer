package com.pp.students_organizer_backend.domain

case class Assignment(id: Long,
                      name: String,
                      description: String,
                      assignmentType: AssignmentType,
                      isDone: Boolean,
                      endDateTimestamp: Long,
                      tasks: List[Task] = List.empty,
                      materials: List[Material] = List.empty,
                     )
