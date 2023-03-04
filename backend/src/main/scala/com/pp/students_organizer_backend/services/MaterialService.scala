package com.pp.students_organizer_backend.services

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.syntax.all.{toFlatMapOps, toFunctorOps}
import com.pp.students_organizer_backend.domain.{AssignmentId, MaterialEntity, MaterialId, StudentId}
import com.pp.students_organizer_backend.services.database.DatabaseCodec.Assignment.assignmentId
import com.pp.students_organizer_backend.services.database.DatabaseCodec.Material.{materialId, materialName, materialUrl}
import com.pp.students_organizer_backend.services.database.DatabaseCodec.Student.studentId
import skunk.*
import skunk.codec.all.{int4, uuid, varchar}
import skunk.implicits.{sql, toIdOps}

trait MaterialService[F[_]]:
  def getAll(studentId: StudentId): F[List[MaterialEntity]]
  def getAllBy(assignmentId: AssignmentId): F[List[MaterialEntity]]
  def insert(material: MaterialEntity): F[Unit]
  def remove(materialId: MaterialId, studentId: StudentId): F[Unit]

object MaterialService:
  def make[F[_]: Concurrent](
      database: Resource[F, Session[F]]
  ): MaterialService[F] =
    new MaterialService[F]:
      override def getAll(studentId: StudentId): F[List[MaterialEntity]] =
        database.use { session =>
          session
            .prepare(ServiceSQL.getAllQuery)
            .flatMap(_.stream(studentId, 1024).compile.toList)
        }

      override def getAllBy(
          assignmentId: AssignmentId
      ): F[List[MaterialEntity]] =
        database.use { session =>
          session
            .prepare(ServiceSQL.getAllByAssignmentIdQuery)
            .flatMap(_.stream(assignmentId, 1024).compile.toList)
        }

      override def insert(material: MaterialEntity): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.insertCommand)
            .flatMap(_.execute(material))
            .void
        }

      override def remove(
          materialId: MaterialId,
          studentId: StudentId
      ): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.removeCommand)
            .flatMap(_.execute(materialId, studentId))
            .void
        }

  private object ServiceSQL:
    val getAllQuery: Query[StudentId, MaterialEntity] =
      sql"""
        SELECT id, name, url, assignment_id FROM material_with_student
            WHERE student_id = $studentId
         """
        .query(materialId ~ materialName ~ materialUrl ~ assignmentId)
        .gmap[MaterialEntity]

    val getAllByAssignmentIdQuery: Query[AssignmentId, MaterialEntity] =
      sql"SELECT id, name, url, assignment_id FROM material WHERE assignment_id = $assignmentId"
        .query(materialId ~ materialName ~ materialUrl ~ assignmentId)
        .gmap[MaterialEntity]

    val insertCommand: Command[MaterialEntity] =
      sql"INSERT INTO material (id, name, url, assignment_id) VALUES ($materialId, $materialName, $materialUrl, $assignmentId)".command
        .gcontramap[MaterialEntity]

    val removeCommand: Command[MaterialId ~ StudentId] =
      sql"""DELETE FROM material m
            USING material_with_student mws
            WHERE m.id = mws.id AND m.id=$materialId AND mws.student_id=$studentId""".command
