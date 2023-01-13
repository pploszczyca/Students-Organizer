val Http4sVersion = "0.23.17"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.4.5"
val MunitCatsEffectVersion = "1.0.7"
val skunkVersion = "0.3.2"
val circleVersion = "0.14.3"

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
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
