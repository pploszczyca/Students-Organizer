package com.pp.students_organizer_backend.services

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.syntax.all.{toFlatMapOps, toFunctorOps}
import com.pp.students_organizer_backend.domain.{MaterialEntity, MaterialId}
import com.pp.students_organizer_backend.utils.DatabaseCodes.{materialId, materialName, materialUrl}
import skunk.codec.all.{int4, uuid, varchar}
import skunk.implicits.sql
import skunk.{Command, Query, Session, Void, ~}
import skunk.implicits.toIdOps

trait MaterialService[F[_]]:
  def getAll: F[List[MaterialEntity]]
  def insert(material: MaterialEntity): F[Unit]
  def remove(materialId: MaterialId): F[Unit]

object MaterialService:
  def make[F[_]: Concurrent](
      database: Resource[F, Session[F]]
  ): MaterialService[F] =
    new MaterialService[F]:
      override def getAll: F[List[MaterialEntity]] =
        database.use { session =>
          session.execute(ServiceSQL.getAllQuery)
        }

      override def insert(material: MaterialEntity): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.insertCommand)
            .flatMap(_.execute(material))
            .void
        }

      override def remove(materialId: MaterialId): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.removeCommand)
            .flatMap(_.execute(materialId))
            .void
        }

  private object ServiceSQL:
    val getAllQuery: Query[Void, MaterialEntity] =
      sql"SELECT id, name, url FROM material"
        .query(materialId ~ materialName ~ materialUrl)
        .gmap[MaterialEntity]

    val insertCommand: Command[MaterialEntity] =
      sql"INSERT INTO material (id, name, url) VALUES ($materialId, $materialName, $materialUrl)".command
        .gcontramap[MaterialEntity]

    val removeCommand: Command[MaterialId] =
      sql"DELETE FROM material WHERE id=$materialId".command
