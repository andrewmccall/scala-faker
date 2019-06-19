logLevel := Level.Debug
name := "scala-faker"
organization := "com.andrewmccall.faker"
crossScalaVersions := Seq("2.11.12", "2.12.8")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.6" % Test
libraryDependencies += "org.mockito" %% "mockito-scala" % "1.4.1" % Test
libraryDependencies += "org.mockito" %% "mockito-scala-scalatest" % "1.4.1" % Test
libraryDependencies += "org.yaml" % "snakeyaml" % "1.24"
libraryDependencies += "org.reflections" % "reflections" % "0.9.11"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.8.2"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.8.2"
libraryDependencies += "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0"

libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.7.3"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.5.4"


inThisBuild(List(
  licenses := Seq("Apache-2.0" -> url("https://opensource.org/licenses/Apache-2.0")),
  homepage := Some(url("https://github.com/andrewmccall/scala-faker")),
  developers := List(Developer("andrewmccall", "Andrew McCall", "andrew@andrewmccall.com", url("http://andrewmccall.com"))),
  scmInfo := Some(ScmInfo(url("https://github.com/andrewmccall/scala-faker"), "scm:git:git@github.com:andrewmccall/scala-faker.git")),

  pgpPublicRing := file("./travis/local.pubring.asc"),
  pgpSecretRing := file("./travis/local.secring.asc"),
  releaseEarlyWith := BintrayPublisher
))