name := """car-adverts"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  cache,
  ws,
  filters,
  "com.typesafe.play" %% "play-slick-evolutions" % "2.1.0",
  "com.h2database" % "h2" % "1.4.196",
  "org.scoverage" %% "scalac-scoverage-runtime" % "1.3.0",
  "org.scalatest" %% "scalatest" % "3.0.3" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

fork in run := true

coverageEnabled := true

coverageExcludedPackages := "<empty>;controllers.javascript.*;router.*;"

coverageMinimum := 85

coverageFailOnMinimum := true
