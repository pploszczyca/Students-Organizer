package com.pp.students_organizer_backend.utils.auth

import cats.effect.Sync
import cats.effect.kernel.Concurrent
import cats.syntax.all.{toFlatMapOps, toFunctorOps}
import dev.profunktor.auth.jwt.{JwtSecretKey, JwtToken, jwtEncode}
import io.circe.syntax.EncoderOps
import pdi.jwt.{JwtAlgorithm, JwtClaim}

import java.time.Clock
import java.util.UUID

trait TokenUtils[F[_]]:
  def createToken: F[JwtToken]

object TokenUtils:
  def make[F[_]: Concurrent](
      tokenExpirationProvider: TokenExpirationProvider[F]
  ): TokenUtils[F] =
    new TokenUtils[F]:
      private val secretKeyValue = "secretKey"

      private implicit val clock: Clock = Clock.systemUTC()

      override def createToken: F[JwtToken] =
        for {
          tokenExpiration <- tokenExpirationProvider.provide
          uuid = UUID.randomUUID
          claim = JwtClaim(uuid.asJson.noSpaces)
            .expiresIn(tokenExpiration.value.toSeconds)
          secretKey = JwtSecretKey(secretKeyValue)
          token <- jwtEncode[F](claim, secretKey, JwtAlgorithm.HS256)
        } yield token
