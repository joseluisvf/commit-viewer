package com.joseluisvf.commitviewer.service

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import com.joseluisvf.commitviewer.common.CommitViewerCommon
import com.joseluisvf.commitviewer.domain.commit.{Commit, Commits}
import com.joseluisvf.commitviewer.domain.commithistory.CommitRetrieverFallback
import com.joseluisvf.commitviewer.exception.{ErrorInvalidUrl, ErrorTimeout}
import org.apache.logging.log4j.scala.Logging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.sys.process._
import scala.util.{Failure, Success, Try}

/**
  * Responsible for setting up the HTTP endpoints for commit retrieval, which serve as the sole entry point to the application.
  *
  * The Github API is the first and main approach for commit retrieval. If that fails for any reason, the second approach is
  * to use a fallback script [[com.joseluisvf.commitviewer.domain.commithistory.CommitRetrieverFallback]] to accomplish
  * the same goal.
  *
  * There are two endpoints available. The second endpoint has two variants:
  * Getting a list of commits for a public github repository
  * - http://localhost:12345/commits/<GITHUB_REPOSITORY_URL>
  * Getting a page of commits for a public github repository
  * - http://localhost:12345/commits/<GITHUB_REPOSITORY_URL>/<PAGE_NUMBER>
  * Getting a page of a user-defined amount of commits for a public github repository
  * - http://localhost:12345/commits/<GITHUB_REPOSITORY_URL>/<PAGE_NUMBER>/<COMMITS_PER_PAGE>
  *
  */
object CommitHistoryService extends Directives with Logging {
  // This value sets expectations on how many elements we expect to find after splitting a valid URL on '/'s
  private val EXPECTED_MINIMUM_URL_ELEMENT_COUNT_AFTER_SPLITTING = 5
  val route: Route = setupCommitHistoryRoute ~ setupPaginatedCommitHistoryRoute

  private def setupCommitHistoryRoute: Route = {
    path("commits" / Segment) {
      githubRepositoryUrl =>
        get {
          try {
            val commits: String = getCommitHistoryViaGithubOf(githubRepositoryUrl)
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, commits))
          } catch {
            case githubInvocationException: Exception =>
              logger.error(s"Unable to invoke the Github API directly:\n ${githubInvocationException.getMessage}")
              logger.info(s"Invoking the Github API directly failed. Resorting to using the fallback method.")
              getCommitHistoryOf(githubRepositoryUrl, CommitViewerCommon.DEFAULT_TIMEOUT_MILLISECONDS) match {
                case Success(commits) =>
                  val commitsAsText = Commits.fromCommitsToText(commits)
                  complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, commitsAsText))

                case Failure(e) =>
                  complete(StatusCodes.BadRequest, HttpEntity(ContentTypes.`text/html(UTF-8)`, s"FAIL:<h1>${e.getMessage}</h1>"))
              }
          }
        }
    }
  }

  private def setupPaginatedCommitHistoryRoute: Route = {
    path("commits" / Segment / IntNumber ~ Slash.? ~ IntNumber.?) {
      case (githubRepositoryUrl, pageNumber, maybeResultsPerPage) =>
        get {
          try {
            val commits: String = getCommitHistoryViaGithubOf(githubRepositoryUrl, Some(pageNumber), maybeResultsPerPage)
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, commits))
          } catch {
            case githubInvocationException: Exception =>
              logger.error(s"Unable to invoke the Github API directly:\n ${githubInvocationException.getMessage}")
              logger.info(s"Invoking the Github API directly failed. Resorting to using the fallback method.")
              getCommitHistoryOf(githubRepositoryUrl, CommitViewerCommon.DEFAULT_TIMEOUT_MILLISECONDS) match {
                case Success(commits) =>
                  val paginatedCommits: List[Commit] = maybeResultsPerPage match {
                    case Some(resultsPerPage) => PaginationAssistant(commits, resultsPerPage).getCommitsFor(pageNumber)
                    case None => PaginationAssistant(commits).getCommitsFor(pageNumber)
                  }

                  val commitsAsText = Commits.fromCommitsToText(paginatedCommits)
                  complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, commitsAsText))

                case Failure(e) =>
                  complete(StatusCodes.BadRequest, HttpEntity(ContentTypes.`text/html(UTF-8)`, s"FAIL:<h1>${e.getMessage}</h1>"))
              }
          }
        }
    }
  }

  private[service] def getCommitHistoryViaGithubOf(githubRepositoryUrl: String, maybePageNumber: Option[Int] = None, maybeCommitsPerPage: Option[Int] = None)
  : String = {
    val split = githubRepositoryUrl.split("/")
    require(split.size >= EXPECTED_MINIMUM_URL_ELEMENT_COUNT_AFTER_SPLITTING)
    val owner = split(3)
    val repository = split(4)

    val paginationExtension = maybePageNumber match {
      case Some(pageNumber) =>
        maybeCommitsPerPage match {
          case Some(commitsPerPage) => s"?page=$pageNumber&per_page=$commitsPerPage"
          case None => s"?page=$pageNumber"
        }

      case None => ""
    }

    // scalastyle:off line.size.limit
    val command = Seq("curl", "-H", "Content-Type: application/vnd.github.v3+json", "-H", "authorization: token 304916603ec4b262e15c2d1788db62aa8a969adf", s"https://api.github.com/repos/$owner/$repository/commits$paginationExtension")
    // scalastyle:on line.size.limit
    val result = command.!!
    result
  }

  private[service] def getCommitHistoryOf(githubRepositoryUrl: String, timeoutInMilliseconds: Long = CommitViewerCommon.DEFAULT_TIMEOUT_MILLISECONDS)
  : Try[List[Commit]] = {
    try {
      Await.result(Future(getCommitHistoryOf(githubRepositoryUrl)), timeoutInMilliseconds.milliseconds)
    } catch {
      case _: TimeoutException =>
        Failure(
          ErrorTimeout(CommitViewerCommon.DEFAULT_TIMEOUT_MILLISECONDS)
        )
    }
  }

private[this] def getCommitHistoryOf(githubRepositoryUrl: String)
  : Try[List[Commit]] = {
    if (!isUrlValid(githubRepositoryUrl)) {
      Failure(ErrorInvalidUrl(githubRepositoryUrl))
    } else {
      CommitRetrieverFallback.getCommitHistoryOf(githubRepositoryUrl, CommitViewerCommon.targetDir, CommitViewerCommon.targetFileName) match {
        case Failure(e) => Failure(e)
        case Success(commitsAsText) =>
          Success(Commits.fromTextToCommits(commitsAsText))
      }
    }
  }

  private def isUrlValid(url: String): Boolean = {
    val validUrlPattern = "^https://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$"
    url.matches(validUrlPattern) && url.split("/").length >= EXPECTED_MINIMUM_URL_ELEMENT_COUNT_AFTER_SPLITTING
  }
}
