package com.pp.students_organizer_backend.services.database

import cats.effect.kernel.Async
import cats.effect.{IO, Resource}
import dev.profunktor.redis4cats.effect.MkRedis
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import dev.profunktor.redis4cats.effect.Log.Stdout.*

object RedisProvider:
  def commandsResource[F[_]: MkRedis: Async]: Resource[F, RedisCommands[F, String, String]] =
    Redis[F].utf8("redis://localhost")
