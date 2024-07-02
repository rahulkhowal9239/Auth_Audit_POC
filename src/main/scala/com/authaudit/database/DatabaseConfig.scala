package com.authaudit.database

import cats.effect.{Async, IO, Resource}
import com.typesafe.config.ConfigFactory
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.flywaydb.core.Flyway

/**
 * Case class to encapsulate database configuration parameters.
 * This class simplifies passing around database connection details.
 *
 * @param url JDBC connection string for the database.
 * @param user Database user name.
 * @param password Database password.
 * @param driver JDBC driver class name.
 */
case class DatabaseConfigs(url: String, user: String, password: String, driver: String)

/**
 * Manages database configurations and migrations.
 * This class is responsible for initializing the database connection and performing schema migrations using Flyway.
 *
 * Implicit Requirement:
 * @param ev Evidence of an Async type class instance for IO, required to create and manage the HikariTransactor.
 */
class DatabaseConfig(implicit ev: Async[IO]) {
  // Load the configuration from the default configuration file.
  private val config = ConfigFactory.load()

  // Construct database configurations by reading from the application's configuration file.
  private val dbConfig = DatabaseConfigs(
    url = config.getString("database.url"),
    user = config.getString("database.user"),
    password = config.getString("database.password"),
    driver = config.getString("database.driver")
  )

  /**
   * Performs database schema migrations using Flyway.
   * This method should be called at application startup to ensure the database schema is up to date.
   */
  def migrate(): Unit = {
    val flyway = Flyway.configure().dataSource(dbConfig.url, dbConfig.user, dbConfig.password).load()
    flyway.migrate()
  }

  /**
   * Provides a Resource managing a HikariCP connection pool wrapped in a Doobie Transactor.
   * This resource ensures that the database connections are properly managed and released when no longer needed.
   *
   * The transactor is used by Doobie to perform database operations in a functional way, leveraging the power of Cats Effect.
   *
   * ExecutionContexts.synchronous is used for database operations, which is appropriate for IO-bound tasks.
   */
  val transactor: Resource[IO, HikariTransactor[IO]] = {
    HikariTransactor.newHikariTransactor[IO](
      dbConfig.driver,
      dbConfig.url,
      dbConfig.user,
      dbConfig.password,
      ExecutionContexts.synchronous
    )
  }
}
