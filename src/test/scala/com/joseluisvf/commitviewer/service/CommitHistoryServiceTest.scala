package com.joseluisvf.commitviewer.service

import com.joseluisvf.commitviewer.exception.ErrorInvalidUrl
import org.scalatest.{Matchers, WordSpec}

import scala.util.Failure

class CommitHistoryServiceTest extends WordSpec with Matchers {
  "A CommitHistoryService when retrieving commit history" when {

    "the primary method is used (github api)" when {
      "an invalid url is provided should fail with an ErrorInvalidUrl" in {
        val invalidUrl = "invalidurl"
        CommitHistoryService.getCommitHistoryOf(invalidUrl) shouldEqual Failure(ErrorInvalidUrl(invalidUrl))
      }

      val githubRepositoryUrl = "https://github.com/python/mypy"
      val GITHUB_DEFAULT_COMMITS_PER_PAGE_COUNT = 30

      "pagination is used" when {
        "retrieving the third page of commits for a busy repository then the default number of commits must be returned" in {
          val commits = CommitHistoryService.getCommitHistoryViaGithubOf(githubRepositoryUrl, maybePageNumber = Some(3))
          val commitCount = inferCommitCountFromGithubResponse(commits)
          assert(commitCount == GITHUB_DEFAULT_COMMITS_PER_PAGE_COUNT)
        }

        "retrieving an non-existent page of commits then no commits must be returned" in {
          val commits = CommitHistoryService.getCommitHistoryViaGithubOf(githubRepositoryUrl, maybePageNumber = Some(300))
          val commitCount = inferCommitCountFromGithubResponse(commits)
          assert(commitCount == 0)
        }

        "retrieving the second page of twenty results for a busy repository then the default number of commits must be returned" in {
          val commits = CommitHistoryService.getCommitHistoryViaGithubOf(githubRepositoryUrl, maybePageNumber = Some(3), Some(20))
          val commitCount = inferCommitCountFromGithubResponse(commits)
          assert(commitCount == 20)
        }
      }

      "pagination is not used" when {
        "retrieving the results for a busy repository then the default number of commits must be returned" in {
          val commits = CommitHistoryService.getCommitHistoryViaGithubOf(githubRepositoryUrl)
          val commitCount = inferCommitCountFromGithubResponse(commits)
          assert(commitCount == GITHUB_DEFAULT_COMMITS_PER_PAGE_COUNT)
        }
      }
    }

    "the fallback method is used (fallback script)" when {
      "an invalid url is provided should fail with an ErrorInvalidUrl" in {
        val invalidUrl = "invalidurl"
        CommitHistoryService.getCommitHistoryOf(invalidUrl) shouldEqual Failure(ErrorInvalidUrl(invalidUrl))
      }

      "a valid url is provided for a public repository then the default number of commits must be returned" in {
        val githubRepositoryUrl = "https://github.com/joseluisvf/SEBC"
        val commits = CommitHistoryService.getCommitHistoryOf(githubRepositoryUrl)
        val expectedNumberOfCommitsInRepository = 41

        commits.isSuccess shouldEqual true
        commits.get.size shouldEqual expectedNumberOfCommitsInRepository
      }
    }
  }

  def inferCommitCountFromGithubResponse(githubApiResponse: String): Int = {
    val patternToFind = "\"parents\": "
    githubApiResponse.split(patternToFind, -1).length - 1
  }
}
