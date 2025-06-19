name := "launcher"
organization := "io.github.KawaBaud"
version := "1.0"

scalaVersion := "3.3.5"
crossPaths := false

javacOptions ++= Seq("-source", "8", "-target", "8")

fork := true
run / javaOptions ++= Seq(
  "-Xmx2G",
  "-XX:+UseG1GC"
)

libraryDependencies ++= Seq(
  "org.json" % "json" % "20250107",
  "org.projectlombok" % "lombok" % "1.18.36",
  "com.google.http-client" % "google-http-client" % "1.46.1",
  "commons-codec" % "commons-codec" % "1.18.0",
  "org.slf4j" % "slf4j-api" % "2.0.16",
  "ch.qos.logback" % "logback-classic" % "1.5.16",
  "ch.qos.logback" % "logback-core" % "1.5.16"
)

Compile / mainClass := Some("io.github.KawaBaud.launcher.Launcher")

scalacOptions ++= Seq("-encoding", "UTF-8")
Compile / packageBin / packageOptions += Package.ManifestAttributes(
  "Implementation-Title" -> name.value,
  "Implementation-Version" -> version.value
)

assembly / assemblyJarName := s"${name.value}-${version.value}.jar"
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x                             => MergeStrategy.first
}
