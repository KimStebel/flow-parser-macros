name := "flow-parser-macros"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.7"

scalacOptions in Global ++= Seq("-deprecation")

val scalaTestVersion = "3.0.1"

lazy val macrosImplSettings = Seq(libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "0.4.2",
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
))

lazy val macros_implementations = project.settings(macrosImplSettings)

lazy val root = (project in file(".")).settings(macrosImplSettings).aggregate(macros_implementations).dependsOn(macros_implementations)

val paradiseVersion = "2.1.0-M5"

addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)
