package com.pp.students_organizer_backend.domain

case class StudentEntity(
    id: Int,
    name: String,
    assignments: List[AssignmentEntity] = List.empty
)
