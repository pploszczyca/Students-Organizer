package com.pp.students_organizer_backend.services.database

import cats.effect.{IO, Resource}
import natchez.Trace.Implicits.noop
import skunk.Session

object Database:
  lazy val sessionResource: Resource[IO, Session[IO]] =
    Session.single(
      host = "localhost",
      port = 5432,
      user = "postgres",
      database = "postgres",
      password = Some("password"),
      debug = true
    )
