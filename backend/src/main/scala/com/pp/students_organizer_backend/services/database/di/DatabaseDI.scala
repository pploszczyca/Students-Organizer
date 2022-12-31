package com.pp.students_organizer_backend.services.database.di

import cats.effect.{IO, Resource}
import skunk.Session
import natchez.Trace.Implicits.noop

object DatabaseDI:
  private lazy val _sessionResource: Resource[IO, Session[IO]] =
    Session.single(
      host = "localhost",
      port = 5432,
      user = "postgres",
      database = "postgres",
      password = Some("password")
    )

  def sessionResource: Resource[IO, Session[IO]] = _sessionResource

