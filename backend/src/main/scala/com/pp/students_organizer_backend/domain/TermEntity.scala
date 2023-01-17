package com.pp.students_organizer_backend.domain

case class TermEntity(
    id: Long,
    number: Int,
    year: String,
    subjects: List[SubjectEntity]
)
