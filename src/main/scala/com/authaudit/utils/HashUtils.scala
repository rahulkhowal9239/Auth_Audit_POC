package com.authaudit.utils

import org.mindrot.jbcrypt.BCrypt

/**
 * Utility object for hashing and verifying passwords.
 * Uses the BCrypt hashing algorithm, which is a robust method for securing passwords.
 *
 * BCrypt is advantageous because it incorporates a salt automatically and has a workload factor
 * that can be adjusted to increase the cost of generating hashes, thereby enhancing security
 * as hardware capabilities improve.
 */
object HashUtils {

  /**
   * Hashes a plaintext password using BCrypt with a generated salt.
   *
   * @param password The plaintext password to be hashed.
   * @return A hashed password as a String. This hashed password includes the salt used in the hashing process.
   *
   * Usage:
   *   val hashedPassword = HashUtils.hashPassword("myPassword123")
   * This method is typically called during user registration or when a user changes their password.
   */
  def hashPassword(password: String): String = {
    BCrypt.hashpw(password, BCrypt.gensalt())
  }

  /**
   * Verifies a candidate password against a previously hashed one.
   *
   * @param candidate The plaintext password submitted by a user during login.
   * @param hashed The previously hashed password stored in the database.
   * @return true if the candidate password matches the hashed password, false otherwise.
   *
   * Usage:
   *   if (HashUtils.verifyPassword("userInputPassword", storedHashedPassword)) {
   *     println("Password is correct!")
   *   } else {
   *     println("Invalid password!")
   *   }
   * This method is typically used during the login process to validate a user's entered password against the stored hash.
   */
  def verifyPassword(candidate: String, hashed: String): Boolean = {
    BCrypt.checkpw(candidate, hashed)
  }
}
