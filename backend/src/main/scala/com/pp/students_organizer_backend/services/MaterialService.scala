package com.pp.students_organizer_backend.services

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import com.pp.students_organizer_backend.domain.MaterialEntity
import skunk.Session

trait MaterialService[F[_]]:
  def getAll: F[List[MaterialEntity]]
  def insert(material: MaterialEntity): F[Unit]
  def remove(materialId: Int): F[Unit]

object MaterialService:
  def make[F[_]: Concurrent](
      database: Resource[F, Session[F]]
  ): MaterialService[F] =
    new MaterialService[F]:
      override def getAll: F[List[MaterialEntity]] = ???

      override def insert(material: MaterialEntity): F[Unit] = ???

      override def remove(materialId: Int): F[Unit] = ???
