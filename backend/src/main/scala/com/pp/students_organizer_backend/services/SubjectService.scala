package com.pp.students_organizer_backend.services

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.syntax.all.toFlatMapOps
import com.pp.students_organizer_backend.domain.{
  StudentId,
  SubjectEntity,
  SubjectId
}
import com.pp.students_organizer_backend.services.database.DatabaseCodec.Student.studentId
import com.pp.students_organizer_backend.services.database.DatabaseCodec.Subject.{
  subjectId,
  subjectName
}
import com.pp.students_organizer_backend.services.database.DatabaseCodec.Term.termId
import skunk.implicits.sql
import skunk.{Query, Session, ~}

trait SubjectService[F[_]]:
  def getBy(subjectId: SubjectId, studentId: StudentId): F[Option[SubjectEntity]]

object SubjectService:
  def make[F[_]: Concurrent](
      database: Resource[F, Session[F]]
  ): SubjectService[F] =
    new SubjectService[F]:
      override def getBy(
          subjectId: SubjectId,
          studentId: StudentId
      ): F[Option[SubjectEntity]] =
        database.use { session =>
          session
            .prepare(ServiceSQL.getByIdAndStudentIdQuery)
            .flatMap(_.option(subjectId, studentId))
        }

  private object ServiceSQL:
    val getByIdAndStudentIdQuery: Query[SubjectId ~ StudentId, SubjectEntity] =
      sql"""
         SELECT s.id, s.name, s.term_id FROM subject AS s
            INNER JOIN term t on t.id = s.term_id
            WHERE s.id = $subjectId AND t.student_id = $studentId
           """
        .query(subjectId ~ subjectName ~ termId)
        .gmap[SubjectEntity]
