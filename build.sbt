ThisBuild / scalaVersion := "2.13.10"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """Avispamientos""",
    libraryDependencies ++= Seq(
      guice,
      javaJpa,
      javaJdbc,
      "com.h2database" % "h2" % "1.4.192",
      "org.hibernate" % "hibernate-core" % "5.4.32.Final"
    )
  )