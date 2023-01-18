package com.pp.students_organizer_backend

import cats.effect.{ExitCode, IO, IOApp}
import com.pp.students_organizer_backend.gateways.Gateways
import com.pp.students_organizer_backend.routes.Routes
import com.pp.students_organizer_backend.services.Services
import com.pp.students_organizer_backend.services.database.Database

object Main extends IOApp.Simple:

  def run: IO[Unit] =
    val services = Services.make(database = Database.sessionResource)
    val gateways = Gateways.make(services = services)
    val routes = Routes.make(gateways = gateways)

    Server[IO](routes).stream.compile.drain.as(ExitCode.Success)
