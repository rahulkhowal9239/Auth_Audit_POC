package com.authaudit.authentication

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.authaudit.utils.{AkkaHttpSupport, JsonFormats}
import spray.json.*

import scala.concurrent.Future
import scala.concurrent.duration.*
import scala.util.{Failure, Success}

/**
 * Object to handle Google authentication and email validation via the ZeroBounce API.
 * It extends AkkaHttpSupport for HTTP essentials and JsonFormats for JSON parsing capabilities.
 */
object GoogleAuth extends AkkaHttpSupport with JsonFormats {
  private val zeroBounceApiKey = "2b83973f4ebf42d9bc45a03c26986d20"// pass the API Key

  /**
   * Validates an email using the ZeroBounce API.
   * This method constructs an HTTP request to the ZeroBounce service and parses the response.
   *
   * @param email The email address to be validated.
   * @return A Future that resolves to either an error message or a success object containing detailed validation results.
   */
  def validateEmailWithZeroBounce(email: String): Future[Either[String, ZeroBounceSuccess]] = {
    val requestUrl = s"https://api.zerobounce.net/v2/validate?api_key=$zeroBounceApiKey&email=$email"
    val responseFuture = Http().singleRequest(HttpRequest(uri = requestUrl))

    responseFuture.flatMap { response =>
      response.entity.toStrict(5.seconds).map { entity =>
        val jsonString = entity.data.utf8String
        println(s"ZeroBounce response: $jsonString")  // Debugging log for response content
        response.status match {
          case StatusCodes.OK =>
            JsonParser(jsonString).convertTo[ZeroBounceSuccess]
            Right(JsonParser(jsonString).convertTo[ZeroBounceSuccess])
          case _ =>
            Left(s"Error response from ZeroBounce: $jsonString")
        }
      }
    }
  }

  /**
   * HTTP route for email validation.
   * Provides an endpoint for external requests to validate emails via the ZeroBounce API.
   */
  val route: Route =
    path("validate" / Segment) { email =>
      onComplete(validateEmailWithZeroBounce(email)) {
        case Success(Right(res)) =>
          if (res.status == "valid") {
            complete(HttpResponse(StatusCodes.OK, entity = s"Email ${res.address} exists as a Google account."))
          } else {
            complete(HttpResponse(StatusCodes.NotFound, entity = s"Email ${res.address} does not exist as a Google account."))
          }
        case Success(Left(error)) =>
          complete(HttpResponse(StatusCodes.BadRequest, entity = s"ZeroBounce error: $error"))
        case Failure(ex) =>
          complete(HttpResponse(StatusCodes.InternalServerError, entity = ex.getMessage))
      }
    }
}
