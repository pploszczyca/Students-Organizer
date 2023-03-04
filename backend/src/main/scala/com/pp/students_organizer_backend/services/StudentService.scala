package com.pp.students_organizer_backend.services

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.syntax.all.{toFlatMapOps, toFoldableOps, toFunctorOps}
import com.pp.students_organizer_backend.domain.{StudentEntity, StudentName}
import com.pp.students_organizer_backend.services.database.DatabaseCodec.Student.{studentId, studentName, studentPassword}
import skunk.implicits.sql
import skunk.{Command, Query, Session}

import scala.language.postfixOps

trait StudentService[F[_]]:
  def getBy(studentName: StudentName): F[Option[StudentEntity]]
  def insert(studentEntity: StudentEntity): F[Unit]

object StudentService:
  def make[F[_]: Concurrent](
      database: Resource[F, Session[F]]
  ): StudentService[F] =
    new StudentService[F]:
      override def getBy(studentName: StudentName): F[Option[StudentEntity]] =
        database.use { session =>
          session
            .prepare(ServiceSQL.getByStudentNameQuery)
            .flatMap(_.option(studentName))
        }

      override def insert(studentEntity: StudentEntity): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.insertCommand)
            .flatMap(_.execute(studentEntity))
            .void
        }

  private object ServiceSQL:
    val getByStudentNameQuery: Query[StudentName, StudentEntity] =
      sql"""
           SELECT id, name, password FROM student
            WHERE name = $studentName
         """
        .query(studentId ~ studentName ~ studentPassword)
        .gmap[StudentEntity]

    val insertCommand: Command[StudentEntity] =
      sql"""
           INSERT INTO student (id, name, password)
            VALUES ($studentId, $studentName, $studentPassword)
         """.command
        .gcontramap[StudentEntity]
