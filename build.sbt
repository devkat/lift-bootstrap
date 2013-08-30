resolvers += Classpaths.typesafeResolver

organization := "net.devkat"

name := "lift-bootstrap"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-webkit" % "2.5",
  "se.fishtank" %% "css-selectors-scala" % "0.1.2"
)


