package com.authaudit.utils

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor

/**
 * Provides essential Akka components as implicit values for Akka HTTP applications.
 * This trait initializes and exposes an ActorSystem, an ActorMaterializer, and an ExecutionContextExecutor,
 * which are required for running Akka HTTP routes and streams.
 *
 * Usage:
 * - Extend this trait in services or controllers that need to perform asynchronous operations
 *   or handle HTTP requests using Akka HTTP.
 */
trait AkkaHttpSupport {
  /**
   * ActorSystem serves as a heavyweight structure that will house your Actors.
   * It is used to manage the lifecycle of the actors and to support system capabilities like configuration,
   * logging, and dispatching.
   */
  implicit val system: ActorSystem = ActorSystem("AuthAuditPOC")

  /**
   * ActorMaterializer is responsible for materializing an Akka Stream's blueprint into a running stream.
   * This materializer is bound to the lifecycle of the ActorSystem.
   */
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  /**
   * Provides an execution context that is bound to the ActorSystem. It is used to execute Futures and other
   * asynchronous tasks.
   */
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
}
