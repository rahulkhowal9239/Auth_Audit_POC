package com.authaudit.utils

import com.authaudit.authentication.{AuditLog, ErrorResponse, GoogleLoginRequest, LoginRequest, User, ZeroBounceError, ZeroBounceSuccess}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
 * The JsonFormats trait extends the DefaultJsonProtocol provided by spray-json.
 * It provides implicit format instances necessary for converting domain objects to and from JSON.
 * This approach encapsulates all JSON format definitions in a single trait, promoting reusability and maintainability.
 *
 * Each implicit val defined here is a RootJsonFormat for a specific data model, enabling automatic
 * serialization and deserialization of these models when sending or receiving JSON data in HTTP requests and responses.
 */
trait JsonFormats extends DefaultJsonProtocol {
  /**
   * JSON format for ErrorResponse.
   * Converts ErrorResponse objects to and from JSON, facilitating error handling in the application.
   */
  implicit val ErrorRequestFormat: RootJsonFormat[ErrorResponse] = jsonFormat2(ErrorResponse)

  /**
   * JSON format for User.
   * Supports JSON serialization of User details, crucial for user-related operations like authentication and profile management.
   */
  implicit val UserRequestFormat: RootJsonFormat[User] = jsonFormat4(User)

  /**
   * JSON format for LoginRequest.
   * Used for parsing login data received from clients, ensuring that login requests can be deserialized correctly.
   */
  implicit val loginRequestFormat: RootJsonFormat[LoginRequest] = jsonFormat2(LoginRequest)

  /**
   * JSON format for GoogleLoginRequest.
   * Specifically handles the serialization of Google-specific login requests, aiding in integration with Google services.
   */
  implicit val googleLoginRequestFormat: RootJsonFormat[GoogleLoginRequest] = jsonFormat1(GoogleLoginRequest)

  /**
   * JSON format for ZeroBounceSuccess.
   * Facilitates the handling of successful responses from the ZeroBounce API, which are used to validate email addresses.
   * Note: Adjust the jsonFormat19 constructor to match the actual number of fields in ZeroBounceSuccess.
   */
  implicit val zeroBounceSuccessFormat: RootJsonFormat[ZeroBounceSuccess] = jsonFormat19(ZeroBounceSuccess)

  /**
   * JSON format for ZeroBounceError.
   * Ensures that any errors returned from the ZeroBounce API can be properly parsed into ZeroBounceError objects.
   */
  implicit val zeroBounceErrorFormat: RootJsonFormat[ZeroBounceError] = jsonFormat1(ZeroBounceError)

  implicit val auditLogFormat: RootJsonFormat[AuditLog] = jsonFormat10(AuditLog)
  implicit val auditLogListFormat: RootJsonFormat[List[AuditLog]] = listFormat[AuditLog]
}
