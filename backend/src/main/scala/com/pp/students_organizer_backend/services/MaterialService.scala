package com.pp.students_organizer_backend.services

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.syntax.all.{toFlatMapOps, toFunctorOps}
import com.pp.students_organizer_backend.domain.MaterialEntity
import skunk.codec.all.{int4, uuid, varchar}
import skunk.implicits.sql
import skunk.{Command, Query, Session, Void, ~}

trait MaterialService[F[_]]:
  def getAll: F[List[MaterialEntity]]
  def insert(material: MaterialEntity): F[Unit]
  def remove(materialId: Int): F[Unit]

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

      override def remove(materialId: Int): F[Unit] =
        database.use { session =>
          session
            .prepare(ServiceSQL.removeCommand)
            .flatMap(_.execute(materialId))
            .void
        }

  private object ServiceSQL:
    val getAllQuery: Query[Void, MaterialEntity] =
      sql"SELECT id, name, url FROM material"
        .query(uuid ~ varchar ~ varchar)
        .map { case id ~ name ~ url => MaterialEntity.create(id, name, url).getOrElse(MaterialEntity.empty()) }

    val insertCommand: Command[MaterialEntity] =
      sql"INSERT INTO material (id, name, url) VALUES ($uuid, $varchar, $varchar)".command
        .contramap(material => ((material.id.value, material.name.value), material.url.value))

    val removeCommand: Command[Int] =
      sql"DELETE FROM material WHERE id=%$int4".command
