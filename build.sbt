name := "simple-tile-game-ai"

version := "1.0.0"

scalaVersion := "2.12.8"

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
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Ywarn-unused-import"
)


libraryDependencies ++= {
  val scalaLoggingV = "3.9.2"
  val scalaTestV = "3.0.6"

  Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV,
    "org.scalatest" %% "scalatest" % scalaTestV % Test
  )
}

// scoverage keys
coverageMinimum := 60.0
coverageFailOnMinimum := true
