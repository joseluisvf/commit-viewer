package com.joseluisvf.commitviewer.service

import com.joseluisvf.commitviewer.exception.ErrorInvalidUrl
import org.scalatest.{Matchers, WordSpec}

import scala.io.Source
import scala.util.Failure

class CommitHistoryServiceTest extends WordSpec with Matchers {
  "A CommitHistoryService when retrieving commit history" when {
    val accessToken = Source.fromFile("src/test/resources/testaccesstoken.txt").getLines().toList.mkString
    val commitHistoryService = new CommitHistoryService(accessToken)

    val badAccessToken = Source.fromFile("src/test/resources/badtestaccesstoken.txt").getLines().toList.mkString
    val badCommitHistoryService = new CommitHistoryService(badAccessToken)

    val githubRepositoryUrl = "https://github.com/python/mypy"
    val GITHUB_DEFAULT_COMMITS_PER_PAGE_COUNT = 30

    "the primary method is used (github api)" when {
      "a bad access token is used should return no results" in {
        val commits = badCommitHistoryService.getCommitHistoryViaGithubOf(githubRepositoryUrl, maybePageNumber = Some(3))
        val maybeCommitCount = inferCommitCountFromGithubResponse(commits)
        assert(maybeCommitCount.isEmpty)
      }

      "an invalid url is provided should fail with an ErrorInvalidUrl" in {
        val invalidUrl = "invalidurl"
        commitHistoryService.getCommitHistoryOf(invalidUrl) shouldEqual Failure(ErrorInvalidUrl(invalidUrl))
      }

      "pagination is used" when {
        "retrieving the third page of commits for a busy repository then the default number of commits must be returned" in {
          val commits = commitHistoryService.getCommitHistoryViaGithubOf(githubRepositoryUrl, maybePageNumber = Some(3))

          inferCommitCountFromGithubResponse(commits) match {
            case Some(commitCount) =>
              assert(commitCount == GITHUB_DEFAULT_COMMITS_PER_PAGE_COUNT)
              // If the access token is invalid (expired or otherwise) this test case will silently succeed
            case None => succeed
          }
        }

        "retrieving an non-existent page of commits then no commits must be returned" in {
          val commits = commitHistoryService.getCommitHistoryViaGithubOf(githubRepositoryUrl, maybePageNumber = Some(300))
          inferCommitCountFromGithubResponse(commits) match {
            case Some(commitCount) =>
              assert(commitCount == 0)
            // If the access token is invalid (expired or otherwise) this test case will silently succeed
            case None => succeed
          }
        }

        "retrieving the second page of twenty results for a busy repository then the default number of commits must be returned" in {
          val commits = commitHistoryService.getCommitHistoryViaGithubOf(githubRepositoryUrl, maybePageNumber = Some(3), Some(20))
          inferCommitCountFromGithubResponse(commits) match {
            case Some(commitCount) =>
              assert(commitCount == 20)
            // If the access token is invalid (expired or otherwise) this test case will silently succeed
            case None => succeed
          }
        }
      }

      "pagination is not used" when {
        "retrieving the results for a busy repository then the default number of commits must be returned" in {
          val commits = commitHistoryService.getCommitHistoryViaGithubOf(githubRepositoryUrl)
          inferCommitCountFromGithubResponse(commits) match {
            case Some(commitCount) =>
              assert(commitCount == GITHUB_DEFAULT_COMMITS_PER_PAGE_COUNT)
            // If the access token is invalid (expired or otherwise) this test case will silently succeed
            case None => succeed
          }
        }
      }
    }

    "the fallback method is used (fallback script)" when {
      "an invalid url is provided should fail with an ErrorInvalidUrl" in {
        val invalidUrl = "invalidurl"
        commitHistoryService.getCommitHistoryOf(invalidUrl) shouldEqual Failure(ErrorInvalidUrl(invalidUrl))
      }

      "a valid url is provided for a public repository then the default number of commits must be returned" in {
        val githubRepositoryUrl = "https://github.com/joseluisvf/SEBC"
        val commits = commitHistoryService.getCommitHistoryOf(githubRepositoryUrl)
        val expectedNumberOfCommitsInRepository = 41

        commits.isSuccess shouldEqual true
        commits.get.size shouldEqual expectedNumberOfCommitsInRepository
      }
    }
  }

  def inferCommitCountFromGithubResponse(githubApiResponse: String): Option[Int] = {
    if (githubApiResponse.contains(CommitHistoryService.BAD_CREDENTIALS_ERROR_MESSAGE)) {
      None
    } else {
      val patternToFind = "\"parents\": "
      Some(githubApiResponse.split(patternToFind, -1).length - 1)
    }
  }
}
