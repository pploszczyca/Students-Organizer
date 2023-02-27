package com.pp.students_organizer_backend.services

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.syntax.all.{toFlatMapOps, toFoldableOps, toFunctorOps}
import com.pp.students_organizer_backend.domain.{
  AssignmentEntity,
  AssignmentId,
  StudentId,
  SubjectId
}
import com.pp.students_organizer_backend.services.database.DatabaseCodec.Assignment.*
import com.pp.students_organizer_backend.services.database.DatabaseCodec.AssignmentType.assignmentTypeId
import com.pp.students_organizer_backend.services.database.DatabaseCodec.Student.studentId
import com.pp.students_organizer_backend.services.database.DatabaseCodec.Subject.subjectId
import skunk.implicits.{sql, toIdOps}
import skunk.{Command, Query, Session, ~}

trait AssignmentService[F[_]]:
  def getAllBy(studentId: StudentId): F[List[AssignmentEntity]]
  def get(
      assignmentId: AssignmentId,
      studentId: StudentId
  ): F[Option[AssignmentEntity]]
  def insert(assignmentEntity: AssignmentEntity): F[Unit]
  def update(assignmentEntity: AssignmentEntity): F[Unit]
  def remove(assignmentId: AssignmentId, studentId: StudentId): F[Unit]

object AssignmentService:
  def make[F[_]: Concurrent](
      database: Resource[F, Session[F]]
  ): AssignmentService[F] =
    new AssignmentService[F]:
      override def getAllBy(studentId: StudentId): F[List[AssignmentEntity]] =
        database.use { session =>
          session
            .prepare(ServiceSQL.getAllByStudentIdQuery)
            .flatMap { preparedSession =>
              preparedSession
                .stream(studentId, chunkSize = 1024)
                .compile
                .toList
            }
        }

      override def get(
          assignmentId: AssignmentId,
          studentId: StudentId
      ): F[Option[AssignmentEntity]] =
        database.use { session =>
          session
            .prepare(ServiceSQL.getByIdAndStudentIdQuery)
            .flatMap { preparedSession =>
              preparedSession
                .option(studentId ~ assignmentId)
            }
        }

      override def insert(assignmentEntity: AssignmentEntity): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.insertCommand)
            .flatMap(_.execute(assignmentEntity))
            .void
        }

      override def update(assignmentEntity: AssignmentEntity): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.updateCommand)
            .flatMap(_.execute(assignmentEntity))
            .void
        }

      override def remove(
          assignmentId: AssignmentId,
          studentId: StudentId
      ): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.removeCommand)
            .flatMap(_.execute(assignmentId, studentId))
            .void
        }

  private object ServiceSQL:
    val getAllByStudentIdQuery: Query[StudentId, AssignmentEntity] =
      sql"""
           SELECT a.id, a.name, a.description, a.assignment_type_id, a.status, a.end_date, a.subject_id FROM assignment as a
            INNER JOIN subject s on s.id = a.subject_id
            INNER JOIN term t on t.id = s.term_id
            INNER JOIN student st on st.id = t.student_id
            WHERE st.id = $studentId
           """
        .query(
          assignmentId ~ assignmentName ~ assignmentDescription ~ assignmentTypeId ~ assignmentStatus ~ assignmentEndDate ~ subjectId
        )
        .gmap[AssignmentEntity]

    val getByIdAndStudentIdQuery
        : Query[StudentId ~ AssignmentId, AssignmentEntity] =
      sql"""
           SELECT a.id, a.name, a.description, a.assignment_type_id, a.status, a.end_date, a.subject_id FROM assignment as a
            INNER JOIN subject s on s.id = a.subject_id
            INNER JOIN term t on t.id = s.term_id
            INNER JOIN student st on st.id = t.student_id
            WHERE st.id = $studentId AND a.id = $assignmentId
           """
        .query(
          assignmentId ~ assignmentName ~ assignmentDescription ~ assignmentTypeId ~ assignmentStatus ~ assignmentEndDate ~ subjectId
        )
        .gmap[AssignmentEntity]

    val insertCommand: Command[AssignmentEntity] =
      sql"""
           INSERT INTO assignment (id, name, description, assignment_type_id, status, end_date, subject_id)
           VALUES ($assignmentId, $assignmentName, $assignmentDescription, $assignmentTypeId, $assignmentStatus, $assignmentEndDate, $subjectId)
         """.command
        .gcontramap[AssignmentEntity]

    val updateCommand: Command[AssignmentEntity] =
      sql"""
           UPDATE assignment
            SET name = $assignmentName,
                description = $assignmentDescription,
                assignment_type_id = $assignmentTypeId,
                status = $assignmentStatus,
                end_date = $assignmentEndDate,
                subject_id = $subjectId
            WHERE id = $assignmentId
         """.command
        .contramap { case assignment: AssignmentEntity =>
          assignment.name ~ assignment.description ~ assignment.assignmentTypeId ~ assignment.status
            ~ assignment.endDate ~ assignment.subjectId ~ assignment.id
        }

    val removeCommand: Command[AssignmentId ~ StudentId] =
      sql"""
        DELETE FROM assignment a
               USING assignment_with_student aws
               WHERE a.id = $assignmentId AND a.id = aws.id = a.id AND aws.student_id = $studentId 
           """.command
