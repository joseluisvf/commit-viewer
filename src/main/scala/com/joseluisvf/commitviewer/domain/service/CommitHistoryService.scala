package com.joseluisvf.commitviewer.domain.service

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import com.joseluisvf.commitviewer.common.CommitViewerCommon
import com.joseluisvf.commitviewer.domain.commit.Commits
import com.joseluisvf.commitviewer.domain.commithistory.CommitRetrieverFallback
import com.joseluisvf.commitviewer.exception.ErrorInvalidUrl
import org.apache.logging.log4j.scala.Logging

import scala.util.{Failure, Success, Try}

object CommitHistoryService extends Directives with Logging {
  val route: Route = getCommitHistory

  def getCommitHistory: Route = {
    path("commits" / Segment) {
      githubRepositoryUrl =>
        get {
          getCommitHistoryOf(githubRepositoryUrl) match {
            case Success(commitsAsText) =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, commitsAsText))

            //TODO pagination
            case Failure(e) =>
              complete(StatusCodes.BadRequest, HttpEntity(ContentTypes.`text/html(UTF-8)`, s"FAIL:<h1>${e.getMessage}</h1>"))
          }
        }
    }
  }

  def getCommitHistoryOf(githubRepositoryUrl: String): Try[String] = {
    if (!isUrlValid(githubRepositoryUrl)) {
      Failure(ErrorInvalidUrl(githubRepositoryUrl))
    } else {
      CommitRetrieverFallback.getCommitHistoryOf(githubRepositoryUrl, CommitViewerCommon.targetDir, CommitViewerCommon.targetFileName) match {
        case Failure(e) => Failure(e)
        case Success(commitsAsText) =>
          val commits = Commits.fromTextToCommits(commitsAsText)
          Success(Commits.fromCommitsToText(commits))
      }
    }
  }

  private def isUrlValid(url: String): Boolean = {
    val validUrlPattern = "^https://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$"
    url.matches(validUrlPattern)
  }
}
