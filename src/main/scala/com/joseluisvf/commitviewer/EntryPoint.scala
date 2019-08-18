package com.joseluisvf.commitviewer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation
import akka.stream.ActorMaterializer
import com.joseluisvf.commitviewer.common.CommitViewerCommon
import com.joseluisvf.commitviewer.service.CommitHistoryService
import org.apache.logging.log4j.scala.Logging

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object EntryPoint extends App with Logging with RouteConcatenation {
  setupHttpEndpoint()

  private def setupHttpEndpoint(): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    implicit val materialiser: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    val bindingFuture = Http().bindAndHandle(CommitHistoryService.route, "localhost", CommitViewerCommon.httpEndpointPortNumber)

    logger.info(s"Server online at http://localhost:${CommitViewerCommon.httpEndpointPortNumber}/\nPress RETURN to stop...")
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}

