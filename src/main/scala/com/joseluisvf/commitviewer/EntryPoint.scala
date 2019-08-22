package com.joseluisvf.commitviewer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation
import akka.stream.ActorMaterializer
import com.joseluisvf.commitviewer.common.CommitViewerCommon
import com.joseluisvf.commitviewer.service.CommitHistoryService
import org.apache.logging.log4j.scala.Logging

import scala.concurrent.ExecutionContextExecutor
import scala.io.{Source, StdIn}
import scala.util.{Failure, Success, Try}

/**
  * Responsible for setting up the main entry point of the application, namely a pair of HTTP endpoints.
  *
  * If the path to a file containing a valid Github access token is provided
  */
object EntryPoint extends App with Logging with RouteConcatenation {
  getAccessTokenFromFileIfPresent(args) match {
    case Success(token) => setupHttpEndpoint(token)
    case Failure(e) =>
      logger.error(e.getMessage)
      System.exit(1)
  }

  private def setupHttpEndpoint(githubAccessToken: String): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    implicit val materialiser: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    val commitHistoryService = new CommitHistoryService(githubAccessToken)
    val bindingFuture = Http().bindAndHandle(commitHistoryService.route, "localhost", CommitViewerCommon.httpEndpointPortNumber)

    logger.info(s"Server online at http://localhost:${CommitViewerCommon.httpEndpointPortNumber}/\nPress RETURN to stop...")
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  private def getAccessTokenFromFileIfPresent(args: Array[String])
  : Try[String] = {
    if (args.nonEmpty) {
      val githubAccessTokenFilePath = args(0)
      Try(Source.fromFile(githubAccessTokenFilePath).getLines().toList.mkString)
    } else {
      Success("")
    }
  }
}
