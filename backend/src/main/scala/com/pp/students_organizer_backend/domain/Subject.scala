package com.pp.students_organizer_backend.domain

case class Subject(id: Long,
                   name: String,
                   assignments: List[Assignment],
                  )
