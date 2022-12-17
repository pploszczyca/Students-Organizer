package com.pp.students_organizer_backend.domain

case class SubjectEntity(id: Long,
                         name: String,
                         assignments: List[AssignmentEntity],
                  )
