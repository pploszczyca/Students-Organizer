package com.pp.students_organizer_backend.services

import com.pp.students_organizer_backend.domain.{AssignmentEntity, AssignmentId, StudentId}

trait AssignmentService[F[_]]:
  def getAll(studentId: StudentId): F[List[AssignmentEntity]]
  def insert(assignmentEntity: AssignmentEntity): F[Unit]
  def remove(assignmentId: AssignmentId): F[Unit]
