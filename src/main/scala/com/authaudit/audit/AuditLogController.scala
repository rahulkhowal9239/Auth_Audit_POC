package com.authaudit.audit

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.authaudit.utils.JsonFormats

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Controller for handling HTTP routes related to audit logs.
 * Utilizes Akka HTTP's routing directives to define endpoints for accessing audit log data.
 *
 * @param auditLogService Service class that provides access to audit log data.
 * @param system Implicit ActorSystem, required for managing actors and handling requests.
 * @param ec Implicit ExecutionContext, used for executing asynchronous operations.
 */
class AuditLogController(auditLogService: AuditLogService)(implicit system: ActorSystem, ec: ExecutionContext) extends JsonFormats{

  /**
   * Defines the HTTP routes for audit log operations.
   * This route setup provides an endpoint to retrieve audit logs, demonstrating how to integrate with the audit log service.
   */
  val route: Route = pathPrefix("audit") {
    path("logs") {
      get {
        onComplete(auditLogService.getLogs) {
          case Success(logs) => complete(logs)
          case Failure(ex) =>
            complete(StatusCodes.InternalServerError -> ex.getMessage)
        }
      }
    }
  }
}
