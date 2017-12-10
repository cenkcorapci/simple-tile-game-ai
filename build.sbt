name := "simple-tile-game-ai"

version := "0.0.0"

scalaVersion := "2.12.4"

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
  val akkaV = "2.5.8"
  val scalaLoggingV = "3.7.2"
  val slf4jV = "1.7.21"
  val logbackV = "1.1.7"
  val logbackEncoderV = "4.7"
  val scalaTestV = "3.0.4"
  val mockitoV = "1.10.19"

  Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV,
    "ch.qos.logback" % "logback-core" % logbackV,
    "ch.qos.logback" % "logback-classic" % logbackV,
    "net.logstash.logback" % "logstash-logback-encoder" % logbackEncoderV,
    "org.slf4j" % "jcl-over-slf4j" % slf4jV,
    "org.scalatest" %% "scalatest" % scalaTestV % Test,
    "org.mockito" % "mockito-core" % mockitoV % Test
  )
}

// scoverage keys
coverageMinimum := 0.0
coverageFailOnMinimum := true

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"

assemblyMergeStrategy in assembly := {
  case PathList("application.conf") => MergeStrategy.concat
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

parallelExecution in Test := false
