package com.joseluisvf.commitviewer.domain.service

import com.joseluisvf.commitviewer.common.CommitViewerCommon
import com.joseluisvf.commitviewer.domain.commit.Commits
import com.joseluisvf.commitviewer.domain.commithistory.CommitRetrieverFallback
import com.joseluisvf.commitviewer.exception.ErrorInvalidUrl

import scala.util.{Failure, Success, Try}

object CommitHistoryService {
  // IN: URL
  // OUT: list of commits OR errors
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
