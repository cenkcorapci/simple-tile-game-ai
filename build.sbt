name := "simple-tile-game-ai"

version := "1.0.0"

scalaVersion := "2.13.16"

scalacOptions := Seq(
  "-deprecation",
  "-encoding", "UTF-8", // yes, this is 2 args
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Wdead-code",
  "-Wnumeric-widen",
  "-Wunused:imports"
)


libraryDependencies ++= {
  val scalaLoggingV = "3.9.5"
  val scalaTestV = "3.2.19"

  Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV,
    "org.scalatest" %% "scalatest" % scalaTestV % Test
  )
}

// scoverage keys
coverageMinimum := 60.0
coverageFailOnMinimum := true
