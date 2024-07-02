package com.authaudit

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.authaudit.authentication.UserRepository
import com.authaudit.audit.{AuditLogController, AuditLogRepository, AuditLogService, UserController}
import com.authaudit.database.DatabaseConfig
import com.authaudit.utils.AkkaHttpSupport

/**
 * Main application entry point.
 * Initializes and starts an HTTP server using Akka HTTP, sets up database configurations,
 * and aggregates routes from various controllers.
 */
object App extends App with AkkaHttpSupport {
    val dbConfig = new DatabaseConfig()
    dbConfig.migrate()

    private val userRepository = new UserRepository(dbConfig)
    private val auditLogRepository = new AuditLogRepository(dbConfig)
    val auditLogService = new AuditLogService(auditLogRepository)

    val auditLogController = new AuditLogController(auditLogService)
    private val userController = new UserController(auditLogService, userRepository)

    private val routes = userController.createUserRoute ~ userController.getUserRoute ~
      userController.deleteUserRoute ~ userController.updateUserRoute ~ userController.googleAuthenticatedRoute

    Http().newServerAt("localhost", 8080).bind(routes)
    println(s"Server online at http://localhost:8080/")
}
