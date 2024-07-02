package com.authaudit.authentication

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.authaudit.database.DatabaseConfig
import com.authaudit.utils.HashUtils.hashPassword
import doobie.implicits.*
import doobie.util.transactor.Transactor

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * UserRepository manages user-related database interactions.
 * It abstracts over the details of database access and the execution of SQL commands.
 *
 * @param dbConfig Configuration setup for database access.
 * @param ec Implicit execution context to manage asynchronous operations.
 */
class UserRepository(dbConfig: DatabaseConfig)(implicit ec: ExecutionContext) {

  /**
   * A helper method to manage the use of a transactor over the lifespan of a database operation.
   * @param f A function that takes a Transactor and returns an IO of type A.
   * @return A Future of type A, representing the result of the database operation.
   */
  private def withTransactor[A](f: Transactor[IO] => IO[A]): Future[A] = {
    dbConfig.transactor.use(f).unsafeToFuture()
  }

  /**
   * Finds a user by their username.
   * @param username The username to search for.
   * @return A Future containing an Option of User if found.
   */
  def findUserByUsername(username: String): Future[Option[User]] = {
    withTransactor { transactor =>
      sql"SELECT id, username, password_hash, email, organization_id FROM users WHERE username = $username"
        .query[User]
        .option
        .transact(transactor)
    }
  }

  /**
   * Finds a user by their email.
   * @param email The email to search for.
   * @return A Future containing an Option of User if found.
   */
  def findUserByEmail(email: String): Future[Option[User]] = {
    withTransactor { transactor =>
      sql"SELECT id, username, password_hash, email, organization_id FROM users WHERE email = $email"
        .query[User]
        .option
        .transact(transactor)
    }
  }

  /**
   * Registers a new user in the database.
   * @param user The user data to insert.
   * @return A Future containing either an error response or the ID of the created user.
   */
  def registerUser(user: User): Future[Either[ErrorResponse, Int]] = {
    createUser(user).transform {
      case Success(createdUserId) => Success(Right(createdUserId))
      case Failure(_) => Success(Left(ErrorResponse("Internal server error", 500)))
    }
  }

  /**
   * Checks if a user exists by their username, email, and organization ID.
   * @param username The username to check.
   * @param email The email to check.
   * @param orgID The organization ID to check.
   * @return A Future of Boolean indicating if the user exists.
   */
  def userExists(username: String, email: String, orgID: String): Future[Boolean] = {
    withTransactor { transactor =>
      sql"SELECT COUNT(*) FROM users WHERE username = $username AND email = $email AND organization_id = $orgID"
        .query[Int]
        .option
        .transact(transactor)
        .map(_.exists(_ > 0))
    }
  }

  /**
   * Inserts a new user into the database.
   * @param user The user data to insert.
   * @return A Future containing the number of rows affected (should be 1 if successful).
   */
  private def createUser(user: User): Future[Int] = {
    withTransactor { transactor =>
      sql"INSERT INTO users (username, password_hash, email, organization_id) VALUES (${user.username}, ${hashPassword(user.passwordHash)}, ${user.email}, ${user.orgID})"
        .update
        .run
        .transact(transactor)
    }
  }

  /**
   * Updates an existing user's data.
   * @param email The email of the user to update.
   * @param user The new user data to apply.
   * @return A Future containing either an error response or the updated user.
   */
  def updateUserDetails(email: String, user: User): Future[Either[ErrorResponse, User]] = {
    val updateAction = withTransactor { transactor =>
      sql"UPDATE users SET username = ${user.username}, password_hash = ${user.passwordHash}, email = ${user.email}, organization_id = ${user.orgID} WHERE email = $email"
        .update
        .run
        .transact(transactor)
    }

    updateAction.transform {
      case Success(0) => Success(Left(ErrorResponse("No user found with specified email.", 404)))
      case Success(_) => Success(Right(user))
      case Failure(_) => Success(Left(ErrorResponse("Internal server error", 500)))
    }
  }

  def checkOrgExists(orgID: String): Future[Boolean] = {
     withTransactor { transactor =>
      sql"SELECT EXISTS (SELECT 1 FROM organization WHERE organization_id = $orgID)"
        .query[Boolean]
        .unique
        .transact(transactor)
    }
  }

  /**
   * Deletes a user from the database based on their email.
   * @param email The email of the user to delete.
   * @return A Future containing either an error response or a unit indicating success.
   */
  def removeUser(email: String): Future[Either[ErrorResponse, Unit]] = {
    val deleteAction = withTransactor { transactor =>
      sql"DELETE FROM users WHERE email = $email"
        .update
        .run
        .transact(transactor)
    }

    deleteAction.transform {
      case Success(0) => Success(Left(ErrorResponse("No user found with specified email.", 404)))
      case Success(_) => Success(Right(()))
      case Failure(_) => Success(Left(ErrorResponse("Internal server error", 500)))
    }
  }
}
