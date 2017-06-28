name := """car-adverts"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
  ws,
  filters,
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "com.h2database" % "h2" % "1.4.196",
  "org.scoverage" %% "scalac-scoverage-runtime" % "1.3.0",
  "org.scalatest" %% "scalatest" % "3.0.3" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test
)

resolvers ++= Seq(
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)
)

fork in run := true

coverageEnabled := true

coverageExcludedPackages := "<empty>;controllers.javascript.*;router.*;"

coverageMinimum := 85

coverageFailOnMinimum := true
