package com.pp.students_organizer_backend

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp.Simple:
  def run: IO[Unit] =
    Students_organizer_backendServer.stream[IO].compile.drain.as(ExitCode.Success)

