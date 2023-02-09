package com.pp.students_organizer_backend.services

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import com.pp.students_organizer_backend.domain.{AssignmentEntity, AssignmentId, StudentId}
import skunk.{Query, Session}
import skunk.implicits.{sql, toIdOps}
import com.pp.students_organizer_backend.utils.DatabaseCodec.Assignment.*
import com.pp.students_organizer_backend.utils.DatabaseCodec.AssignmentType.assignmentTypeId
import com.pp.students_organizer_backend.utils.DatabaseCodec.Student.studentId
import com.pp.students_organizer_backend.utils.DatabaseCodec.Subject.subjectId

trait AssignmentService[F[_]]:
  def getAllBy(studentId: StudentId): F[List[AssignmentEntity]]
  def insert(assignmentEntity: AssignmentEntity): F[Unit]
  def remove(assignmentId: AssignmentId): F[Unit]

object AssignmentService:
  def make[F[_]: Concurrent](
      database: Resource[F, Session[F]]
  ): AssignmentService[F] =
    new AssignmentService[F]:
      override def getAllBy(studentId: StudentId): F[List[AssignmentEntity]] = ???

      override def insert(assignmentEntity: AssignmentEntity): F[Unit] = ???

      override def remove(assignmentId: AssignmentId): F[Unit] = ???

  private object ServiceSQL:
    val getAllByStudentIdQuery: Query[StudentId, AssignmentEntity] =
      sql"""
           SELECT a.id, a.name, a.description, a.assignment_type_id, a.status, a.end_date, a.subject_id FROM assignment as a
            INNER JOIN subject s on s.id = a.subject_id
            INNER JOIN term t on t.id = s.term_id
            INNER JOIN student st on st.id = t.student_id
            WHERE st.id = $studentId
           """
        .query(assignmentId ~ assignmentName ~ assignmentDescription ~ assignmentTypeId ~ assignmentStatus ~ assignmentEndDate ~ subjectId)
        .gmap[AssignmentEntity]
