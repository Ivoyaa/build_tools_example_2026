val scala3 = "3.8.4"
val scala2_13 = "2.13.18"
val tapirVersion = "1.12.3"
val munitVersion = "1.1.1"

scalaVersion := scala3
organization := "dev.buildfest"

lazy val buildSignature = taskKey[String]("Cached module signature")

def signatureSettings = Seq(
  buildSignature := s"${name.value}:${version.value} (Scala ${scalaVersion.value})"
)

lazy val core = projectMatrix
  .in(file("core"))
  .jvmPlatform(scalaVersions = Seq(scala2_13, scala3))
  .settings(
    name := "talks-core",
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
    signatureSettings
  )

lazy val server = projectMatrix
  .in(file("server"))
  .dependsOn(core)
  .jvmPlatform(scalaVersions = Seq(scala2_13, scala3))
  .settings(
    name := "talks-server",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-jdkhttp-server" % tapirVersion,
      "org.scalameta" %% "munit" % munitVersion % Test,
      ("com.fasterxml.jackson" % "jackson-bom" % "2.21.0").pomOnly(),
      "com.fasterxml.jackson.core" % "jackson-core" % "*"
    ),
    signatureSettings
  )

lazy val root = rootProject.autoAggregate
