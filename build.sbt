name := """car-adverts"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  filters,
  "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.10.20",
  "com.typesafe.slick" %% "slick" % "3.0.3",
  "net.java.truecommons" % "truecommons-shed" % "2.5.0",
   specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


fork in run := true