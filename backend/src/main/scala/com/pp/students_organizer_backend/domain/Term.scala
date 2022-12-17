package com.pp.students_organizer_backend.domain

case class Term(id: Long,
                number: Int,
                year: String,
                subjects: List[Subject],
               )
