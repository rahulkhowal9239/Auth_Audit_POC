package com.authaudit.authentication

/**
 * Represents a user in the system.
 * @param username The user's chosen username.
 * @param passwordHash The hash of the user's password, for security.
 * @param email The user's email address.
 * @param orgID The identifier for the organization to which the user belongs.
 */
case class User(username: String, passwordHash: String, email: String, orgID: String)

/**
 * Represents a login request.
 * @param username The username provided by the user.
 * @param password The plaintext password provided by the user.
 */
case class LoginRequest(username: String, password: String)

/**
 * Represents a Google login request using an OAuth token.
 * @param token The OAuth token received from Google sign-in.
 */
case class GoogleLoginRequest(token: String)

/**
 * Represents an error response to be sent to the client.
 * @param message A human-readable error message.
 * @param code The HTTP status code associated with the error.
 */
case class ErrorResponse(message: String, code: Int)

/**
 * Represents a successful response from the ZeroBounce API for email validation.
 * Includes detailed attributes about the email address being validated.
 *
 * @param address The email address that was validated.
 * @param status The validation status (e.g., "Valid", "Invalid").
 * @param sub_status More detailed status provided by ZeroBounce.
 * @param free_email Whether the email is from a free email provider.
 * @param did_you_mean Suggestion for possible typos in the email address.
 * @param account The local part of the email (before the '@').
 * @param domain The domain part of the email.
 * @param domain_age_days The age of the domain in days.
 * @param smtp_provider The SMTP service provider, if detected.
 * @param mx_found Whether an MX record was found for the domain.
 * @param mx_record The specific MX record found for the domain.
 * @param firstname The first name associated with the email, if available.
 * @param lastname The last name associated with the email, if available.
 * @param gender The gender associated with the email, if available.
 * @param country The country associated with the email, if available.
 * @param region The region associated with the email, if available.
 * @param city The city associated with the email, if available.
 * @param zipcode The postal code associated with the email, if available.
 * @param processed_at The timestamp when the email was processed.
 */
case class ZeroBounceSuccess(
                              address: String,
                              status: String,
                              sub_status: Option[String],
                              free_email: Boolean,
                              did_you_mean: Option[String],
                              account: Option[String],
                              domain: String,
                              domain_age_days: Option[String],
                              smtp_provider: Option[String],
                              mx_found: String,
                              mx_record: Option[String],
                              firstname: Option[String],
                              lastname: Option[String],
                              gender: Option[String],
                              country: Option[String],
                              region: Option[String],
                              city: Option[String],
                              zipcode: Option[String],
                              processed_at: String
                            )

/**
 * Represents an error response from the ZeroBounce API.
 * @param error A descriptive error message from ZeroBounce.
 */
case class ZeroBounceError(error: String)

case class AuditLog(
                     id: String,
                     organizationId: String,
                     tableName: String,
                     recordId: String,
                     oldData: String,
                     newData: String,
                     ipAddress: String,
                     userAgent: String,
                     userId: String,
                     timestamp: String
                   )