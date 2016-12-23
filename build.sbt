name := "flow-parser-macros"

version := "0.0.1"

scalaVersion := "2.11.7"

lazy val macros_implementations = project

lazy val root = (project in file(".")).aggregate(macros_implementations).dependsOn(macros_implementations)

val paradiseVersion = "2.1.0-M5"

addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)
