package com.joseluisvf.commitviewer.service

import com.joseluisvf.commitviewer.domain.commit.Commit
import org.scalatest.{Matchers, WordSpec}

class PaginationAssistantTest extends WordSpec with Matchers {
  private val commitSeparator = Commit.placeholderSeparator
  private val validCommitAsText = s"abc${commitSeparator}123"
  private val aCommit = Commit(validCommitAsText)
  private val anotherCommit = Commit(validCommitAsText + "4")

  "A PaginationAssistant" when {
    "getPageContents is invoked" when {
      "a number of results per page exceeding the total amount is requested then all results should be returned" in {
        val commits = List(aCommit, aCommit, aCommit)
        val paginationAssistant = PaginationAssistant(commits)

        val result = paginationAssistant.getCommitsFor(1)

        result.size shouldEqual commits.length
      }

      "a page number exceeding the allowed amount is requested then the last page should be returned" in {
        val commits = List(aCommit, aCommit, anotherCommit)
        val paginationAssistant = PaginationAssistant(commits, 1)

        val result = paginationAssistant.getCommitsFor(12)

        result.size shouldEqual 1
        result.head.getSubject shouldEqual "1234"
      }

      "there are not enough items to fill up a single page then they should all be returned" in {
        val commits = List(aCommit, aCommit, aCommit)
        val paginationAssistant = PaginationAssistant(commits, 100)

        val result = paginationAssistant.getCommitsFor(10)

        result.size shouldEqual commits.size
      }

      "a negative page is required then no results are returned" in {
        val commits = List(aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit, aCommit)
        val paginationAssistant = PaginationAssistant(commits)

        val result = paginationAssistant.getCommitsFor(-1)

        result.size shouldEqual 0
      }

      "the last page is requested" when {
        // scalastyle:off line.size.limit
        val aHundredCommits = List(aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit)
        // scalastyle:on line.size.limit

        "the number of results is equal to the amount per page then all results should be returned" in {
          val paginationAssistant = PaginationAssistant(aHundredCommits)
          val result = paginationAssistant.getCommitsFor(9)

          result.size shouldEqual 10
        }

        // scalastyle:off line.size.limit
        "the number of results is equal to the amount per page then all results should be returned, even if they are inferior to the results per page amount" in {
          // scalastyle:on line.size.limit
          val paginationAssistant = PaginationAssistant(aHundredCommits, 9)
          val result = paginationAssistant.getCommitsFor(12)

          result.size shouldEqual 1
        }
      }
    }
  }
}
