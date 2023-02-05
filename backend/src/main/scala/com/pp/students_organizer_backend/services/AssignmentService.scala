package com.pp.students_organizer_backend.services

import com.pp.students_organizer_backend.domain.{AssignmentEntity, AssignmentId}

trait AssignmentService[F[_]]:
  def getAll: F[List[AssignmentEntity]]
  def insert(assignmentEntity: AssignmentEntity): F[Unit]
  def remove(assignmentId: AssignmentId): F[Unit]
