package com.pp.students_organizer_backend.utils.auth

import cats.effect.kernel.Concurrent
import cats.syntax.all.{catsSyntaxApplicativeId, toFunctorOps}
import com.pp.students_organizer_backend.domain.TokenExpiration

import scala.concurrent.duration.{FiniteDuration, MINUTES}

trait TokenExpirationProvider[F[_]]:
  def provide: F[TokenExpiration]

object TokenExpirationProvider:
  def make[F[_]: Concurrent]: TokenExpirationProvider[F] =
    new TokenExpirationProvider[F]:
      private val tokenExpirationDuration = FiniteDuration(10, MINUTES)

      override def provide: F[TokenExpiration] =
        tokenExpirationDuration
          .pure[F]
          .map(TokenExpiration.apply)
