package com.pp.students_organizer_backend

import cats.effect.*
import com.pp.students_organizer_backend.services.database.AssignmentTypeService
import com.pp.students_organizer_backend.services.database.models.AssignmentTypeModel
import natchez.Trace.Implicits.noop
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

object HelloDatabase extends IOApp:
  private val sessionResource: Resource[IO, Session[IO]] =
    Session.single(
      host = "localhost",
      port = 5432,
      user = "postgres",
      database = "postgres",
      password = Some("password")
    )

  def run(args: List[String]): IO[ExitCode] =
    sessionResource.use { session =>
      val service = AssignmentTypeService(session)

      for {
        types <- service.getAssignmentTypes()
        _ <- IO.print(types)
      } yield ExitCode.Success
    }
