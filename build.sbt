resolvers += Classpaths.typesafeResolver

organization := "net.devkat"

name := "lift-bootstrap"

version := "0.1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-webkit" % "2.5",
  "se.fishtank" %% "css-selectors-scala" % "0.1.3"
)


