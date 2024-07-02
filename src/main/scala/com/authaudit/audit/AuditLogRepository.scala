package com.authaudit.audit

import com.authaudit.database.DatabaseConfig
import doobie.implicits.*

import scala.concurrent.{ExecutionContext, Future}
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.authaudit.authentication.AuditLog

/**
 * Repository class for managing audit log entries in the database.
 * This class provides a functional programming interface to query audit logs using Doobie and Cats Effect.
 *
 * @param dbConfig The database configuration providing access to the database transactor.
 * @param ec Implicit execution context for managing asynchronous operations outside of IO monad.
 */
class AuditLogRepository(dbConfig: DatabaseConfig)(implicit ec: ExecutionContext) {

  /**
   * A helper method to manage the use of a Doobie transactor. It encapsulates the pattern of using
   * the transactor to execute database queries wrapped in IO monads and converting them to Scala Futures.
   *
   * @param f A function from Transactor[IO] to IO[A], representing a database operation.
   * @return A Future[A], the result of the database operation, allowing integration with Scala's Future-based APIs.
   */
  private def useTransactor[A](f: doobie.Transactor[IO] => IO[A]): Future[A] = {
    dbConfig.transactor.use(f).unsafeToFuture()
  }

  /**
   * Retrieves all audit logs from the database.
   * This method forms a Doobie query to select all entries from the audit_logs table, mapping them to a List of AuditLog objects.
   *
   * @return A Future containing a List of AuditLog objects, each representing an entry in the audit_logs database table.
   * The Future facilitates asynchronous processing and non-blocking I/O operations, which is crucial for performance in web applications.
   */
  def findAll: Future[List[AuditLog]] = {
    useTransactor { transactor =>
      sql"SELECT * FROM audit_logs".query[AuditLog].to[List].transact(transactor)
    }
  }
}
