name := "scala-faker"
organization := "com.andrewmccall.faker"
version := "1.0-SNAPSHOT"
crossScalaVersions := Seq("2.11.8", "2.12.8")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.6" % Test
libraryDependencies += "org.yaml" % "snakeyaml" % "1.24"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.8.2"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.8.2"
libraryDependencies += "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0"

libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.7.3"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.5.4"