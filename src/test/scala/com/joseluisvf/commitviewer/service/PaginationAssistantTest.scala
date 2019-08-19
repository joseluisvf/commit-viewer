package com.joseluisvf.commitviewer.service

import com.joseluisvf.commitviewer.domain.commit.Commit
import org.scalatest.{Matchers, WordSpec}

// scalastyle:off line.size.limit
class PaginationAssistantTest extends WordSpec with Matchers {
  private val commitSeparator = Commit.placeholderSeparator
  private val validCommitAsText = s"abc${commitSeparator}123"
  private val aCommit = Commit(validCommitAsText)
  private val anotherCommit = Commit(validCommitAsText + "4")


  "A PaginationAssistant" when {
    "getPageContents is invoked" when {
      "a number of results per page exceeding the total amount is requested then all results should be returned" in {
        val commits = List(aCommit, aCommit, aCommit)
        val paginationAssistant = PaginationAssistant(commits, 10)

        val result = paginationAssistant.getResultsFor(1)

        result.size shouldEqual commits.length
      }

      "a page number exceeding the allowed amount is requested then the last page should be returned" in {
        val commits = List(aCommit, aCommit, anotherCommit)
        val paginationAssistant = PaginationAssistant(commits, 1)

        val result = paginationAssistant.getResultsFor(12)

        result.size shouldEqual 1
        result.head.getSubject shouldEqual "1234"
      }
      "there are not enough items to fill up a single page then they should all be returned" in {
        val commits = List(aCommit, aCommit, aCommit)
        val paginationAssistant = PaginationAssistant(commits, 100)

        val result = paginationAssistant.getResultsFor(10)

        result.size shouldEqual commits.size
      }

      "a negative page is required then no results are returned" in {
        val commits = List(aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit,aCommit, aCommit)
        val paginationAssistant = PaginationAssistant(commits, 10)

        val result = paginationAssistant.getResultsFor(-1)

        result.size shouldEqual 0
      }

      "the last page is requested" when {
        val aHundredCommits = List(aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit, aCommit)

        "the number of results is equal to the amount per page then all results should be returned" in {
          val paginationAssistant = PaginationAssistant(aHundredCommits, 10)
          val result = paginationAssistant.getResultsFor(9)

          result.size shouldEqual 10
        }

        "even if they are inferior to the results per page amount" in {
          val paginationAssistant = PaginationAssistant(aHundredCommits, 9)
          val result = paginationAssistant.getResultsFor(12)

          result.size shouldEqual 1
        }
      }
    }
  }
}
