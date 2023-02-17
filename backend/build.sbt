val Http4sVersion = "0.23.18"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.4.5"
val MunitCatsEffectVersion = "1.0.7"
val skunkVersion = "0.5.1"
val circleVersion = "0.14.3"
val scalaTestVersion = "3.2.15"
val mockitoVersion = "3.2.15.0"
val jwtVersion = "9.2.0"
val redisVersion = "1.4.0"
val jwtAuthVersion = "1.2.0"

lazy val root = (project in file("."))
  .settings(
    organization := "com.pp",
    name := "students_organizer_backend",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.2.1",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.scalameta"   %% "munit"               % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "org.tpolecat" %% "skunk-core" % skunkVersion,
      "io.circe" %% "circe-generic" % circleVersion,
      "io.circe" %% "circe-literal" % circleVersion,
      "com.github.jwt-scala" %% "jwt-core" % jwtVersion,
      "dev.profunktor" %% "redis4cats-effects" % redisVersion,
      "dev.profunktor" %% "http4s-jwt-auth" % jwtAuthVersion,
      "org.scalatest" %% "scalatest-flatspec" % scalaTestVersion % "test",
      "org.scalatestplus" %% "mockito-4-6" % mockitoVersion % "test",
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
