package com.pp.students_organizer_backend.domain

case class AssignmentTypeEntity(id: Int, name: String)

object AssignmentTypeEntity:
  private val NO_ID_VALUE = -1

  def apply(name: String): AssignmentTypeEntity =
    AssignmentTypeEntity(NO_ID_VALUE, name)
