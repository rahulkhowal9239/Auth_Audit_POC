package com.authaudit.audit

import akka.actor.ActorSystem
import com.authaudit.authentication.AuditLog

import scala.concurrent.{ExecutionContext, Future}

/**
 * Provides business logic related to audit logs.
 * This service class handles high-level operations and transformations on data retrieved from the AuditLogRepository.
 *
 * @param auditLogRepo The repository associated with audit log data.
 * @param system Implicit ActorSystem to manage actors and other concurrency needs within Akka.
 * @param ec Implicit ExecutionContext to handle asynchronous operations, allowing for non-blocking execution.
 */
class AuditLogService(auditLogRepo: AuditLogRepository)(implicit system: ActorSystem, ec: ExecutionContext) {

  /**
   * Retrieves all audit logs from the repository.
   * This method encapsulates the logic to interact with the AuditLogRepository to fetch all available audit logs.
   * It returns a Future that will complete with a List of AuditLog objects when the data is available,
   * allowing for asynchronous retrieval.
   *
   * @return A Future containing a list of AuditLog objects representing all the logs stored in the repository.
   * The use of Future facilitates handling potential delays in data fetching or processing asynchronously,
   * improving the responsiveness of the application.
   */
  def getLogs: Future[List[AuditLog]] = {
    auditLogRepo.findAll
  }
}
