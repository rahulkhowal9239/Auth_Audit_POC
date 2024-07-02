package com.authaudit.audit

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.authaudit.authentication.GoogleAuth.validateEmailWithZeroBounce
import com.authaudit.authentication.{GoogleAuth, User, UserRepository}
import com.authaudit.utils.JsonFormats
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

/**
 * Controls HTTP routes related to user operations.
 * Provides functionality for creating, retrieving, updating, and deleting users.
 * Integrates email validation using the ZeroBounce API for certain user actions.
 *
 * @param auditLogService Service for managing audit logs.
 * @param userRepo Repository for user data access.
 * @param ec Implicit execution context for managing asynchronous operations.
 * @param system Implicit actor system for managing actors and system-level operations.
 */
class UserController(auditLogService: AuditLogService, userRepo: UserRepository)
                    (implicit ec: ExecutionContext, system: ActorSystem)
  extends JsonFormats with LazyLogging {

  /**
   * Route to create a new user. Checks for organization existence and user uniqueness.
   * POST /user
   */

  def createUserRoute: Route = {
    path("user") {
      post {
        entity(as[User]) { user =>
          val checksFuture: Future[(Boolean, Boolean)] = for {
            orgExists <- userRepo.checkOrgExists(user.orgID)
            userExists <- userRepo.userExists(user.username, user.email, user.orgID)
          } yield (orgExists, userExists)

          onSuccess(checksFuture) {
            case (false, _) => complete(StatusCodes.BadRequest, s"Organization with ID ${user.orgID} does not exist.")
            case (_, true) => complete(StatusCodes.BadRequest, s"User with username ${user.username}, email ${user.email}, and orgID ${user.orgID} already exists.")
            case _ => onSuccess(userRepo.registerUser(user)) {
              case Right(createdUserId) => complete(StatusCodes.Created, s"User created with ID: $createdUserId")
              case Left(errorResponse) => complete(StatusCodes.BadRequest, errorResponse.message)
            }
          }
        }
      }
    }
  }

  /**
   * Route to create a user with Google authentication after email validation with ZeroBounce.
   * POST /authenticated/user
   */
  def googleAuthenticatedRoute: Route = {
    pathPrefix("authenticated") {
      path("user") {
        post {
          entity(as[User]) { user =>
            onComplete(validateEmailWithZeroBounce(user.email)) {
              case Success(Right(res)) if res.status == "valid" =>
                onSuccess(userRepo.registerUser(user)) {
                  case Right(createdUserId) => complete(StatusCodes.Created, s"User created with ID: $createdUserId")
                  case Left(errorResponse) => complete(StatusCodes.BadRequest, errorResponse.message)
                }
              case _ => complete(StatusCodes.BadRequest, "Email Validation Failed Or You Did Not Enter Correct API Key.")
            }
          }
        }
      }
    }
  }

  /**
   * Route to retrieve a user by email.
   * GET /user/{email}
   */
  def getUserRoute: Route = {
    path("user" / Segment) { email =>
      get {
        onSuccess(userRepo.findUserByEmail(email)) {
          case Some(user) => complete(StatusCodes.OK, user)
          case None => complete(StatusCodes.NotFound, "User not found.")
        }
      }
    }
  }

  /**
   * Route to update user details by email.
   * PUT /user/{email}
   */
  def updateUserRoute: Route = {
    path("user" / Segment) { email =>
      put {
        entity(as[User]) { updateUser =>
          onSuccess(userRepo.updateUserDetails(email, updateUser)) {
            case Right(updatedUser) => complete(StatusCodes.OK, updatedUser)
            case Left(error) => complete(StatusCodes.BadRequest, error.message)
          }
        }
      }
    }
  }

  /**
   * Route to delete a user by email.
   * DELETE /user/{email}
   */
  def deleteUserRoute: Route = {
    path("user" / Segment) { email =>
      delete {
        onSuccess(userRepo.removeUser(email)) {
          case Right(_) => complete(StatusCodes.OK, "User deleted successfully.")
          case Left(error) => complete(StatusCodes.BadRequest, error.message)
        }
      }
    }
  }
}
