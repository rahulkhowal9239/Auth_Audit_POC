ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "AuthAuditPOC",
    libraryDependencies ++= Seq(
      // Akka HTTP for building reactive web servers
      "com.typesafe.akka" %% "akka-http" % "10.5.3",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
      "com.typesafe.akka" %% "akka-stream" % "2.8.5",

      // JSON handling with Spray
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.3",
      "io.spray" %% "spray-json" % "1.3.6",

      // PostgreSQL JDBC driver
      "org.postgresql" % "postgresql" % "42.7.3",

      // Database migration tool
      "org.flywaydb" % "flyway-core" % "7.15.0",

      // Functional programming libraries
      "org.typelevel" %% "cats-core" % "2.6.1",
      "org.typelevel" %% "cats-effect" % "3.3.13",

      // Doobie for database access
      "org.tpolecat" %% "doobie-core" % "1.0.0-M5",
      "org.tpolecat" %% "doobie-postgres" % "1.0.0-M5",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-M5",

      // Configuration management
      "com.typesafe" % "config" % "1.4.2",

      // Password hashing utility
      "org.mindrot" % "jbcrypt" % "0.4",

      // Logging
      "ch.qos.logback" % "logback-classic" % "1.2.10",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",

      // Google API Client libraries
      "com.google.api-client" % "google-api-client" % "1.30.9",
      "com.google.oauth-client" % "google-oauth-client-jetty" % "1.30.5",
      "com.google.apis" % "google-api-services-gmail" % "v1-rev110-1.25.0"
    )
  )
