package com.pp.students_organizer_backend

import cats.effect.kernel.Async
import cats.effect.{Async, Resource}
import cats.syntax.all.*
import com.comcast.ip4s.*
import com.pp.students_organizer_backend.routes.Routes
import com.pp.students_organizer_backend.routes.assignmentType.AssignmentTypeRoutes
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger

class Server[F[_]: Async](private val routes: Routes[F]):
  def stream: Stream[F, Nothing] = {
    val httpApp = routes.allRoutes.orNotFound
    val finalHttpApp = Logger.httpApp(true, true)(httpApp)

    for {
      exitCode <- Stream.resource(
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build >>
          Resource.eval(Async[F].never)
      )
    } yield exitCode
  }.drain
