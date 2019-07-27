package com.joseluisvf.commitviewer.domain.commithistory

import com.joseluisvf.commitviewer.exception.ErrorRepositoryNotFoundFor
import org.scalatest.{Matchers, WordSpec}

import scala.util.Failure

class CommitRetrieverFallbackTest extends WordSpec with Matchers {
  "A fallback commit retriever" when {
    "getCommitHistory is invoked" when {
      val targetDir: String = s"${System.getProperty("user.dir")}/src/main/resources/commit_history_result/tmp"
      val fileName: String = "test-commit-history.txt"

      "a valid github url for an existing repository is provided" should {
        val validGithubUrl: String = "https://github.com/joseluisvf/commit-viewer"

        "return the commit history" in {
          val maybeCommitHistory =
            CommitRetrieverFallback.getCommitHistoryOf(
              validGithubUrl,
              targetDir,
              fileName)

          assert(maybeCommitHistory.isSuccess)
        }

        "return the commit history even if an extra / is appended to the url" in {
          val validGithubUrlWithExtraForwardSlash = validGithubUrl + "/"

          val maybeCommitHistory =
            CommitRetrieverFallback.getCommitHistoryOf(
              validGithubUrlWithExtraForwardSlash,
              targetDir,
              fileName)

          assert(maybeCommitHistory.isSuccess)
        }
      }

      "a valid github url for a non-existing repository is provided" should {
        "fail with a ErrorRepositoryNotFound" in {
          val nonExistingRepositoryUrl = "https://github.com/somecompanythatdoesnotexist/arepo"

          val maybeCommitHistory =
            CommitRetrieverFallback.getCommitHistoryOf(
              nonExistingRepositoryUrl,
              targetDir,
              fileName)

          maybeCommitHistory.shouldEqual(Failure(ErrorRepositoryNotFoundFor(nonExistingRepositoryUrl)))
        }
      }
    }
  }
}
