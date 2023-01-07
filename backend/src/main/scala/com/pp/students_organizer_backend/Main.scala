package com.pp.students_organizer_backend

import cats.effect.{ExitCode, IO, IOApp}
import com.pp.students_organizer_backend.routes.di.RoutesDI
import com.pp.students_organizer_backend.services.database.di.DatabaseDI

object Main extends IOApp.Simple:

  def run: IO[Unit] =
    Server[IO](
      assignmentTypeRoutes = RoutesDI.assignmentTypeRoutes(
        databaseResource = DatabaseDI.sessionResource,
      )
    ).stream.compile.drain.as(ExitCode.Success)
