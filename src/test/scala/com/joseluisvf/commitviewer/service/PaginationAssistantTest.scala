package com.joseluisvf.commitviewer.service

import com.joseluisvf.commitviewer.domain.commit.Commit
import org.scalatest.{Matchers, WordSpec}

class PaginationAssistantTest extends WordSpec with Matchers {
  private val commitSeparator = Commit.placeholderSeparator
  private val validCommitAsText = s"abc${commitSeparator}123"
  private val aCommit = Commit(validCommitAsText)
  private val anotherCommit = Commit(validCommitAsText + "4")
  private val threeCommits = generateCommits(3)
  private val fifteenCommits = generateCommits(15)
  private val aHundredCommits = generateCommits(100)

  "A PaginationAssistant" when {
    "getPageContents is invoked" when {
      "a number of results per page exceeding the total amount is requested then all results should be returned" in {
        val paginationAssistant = PaginationAssistant(threeCommits)

        val result = paginationAssistant.getCommitsForPageNumber(1)

        result.size shouldEqual threeCommits.length
      }

      "a page number exceeding the allowed amount is requested then the last page should be returned" in {
        val commits = List(aCommit, aCommit, anotherCommit)
        val paginationAssistant = PaginationAssistant(commits, 1)

        val result = paginationAssistant.getCommitsForPageNumber(12)

        result.size shouldEqual 1
        result.head.getSubject shouldEqual "1234"
      }

      "there are not enough items to fill up a single page then they should all be returned" in {
        val paginationAssistant = PaginationAssistant(threeCommits, 100)

        val result = paginationAssistant.getCommitsForPageNumber(10)

        result.size shouldEqual threeCommits.size
      }

      "a negative page is required then no results are returned" in {
        val paginationAssistant = PaginationAssistant(fifteenCommits)

        val result = paginationAssistant.getCommitsForPageNumber(-1)

        result.size shouldEqual 0
      }

      "the last page is requested" when {
        "the number of results is equal to the amount per page then all results should be returned" in {
          val paginationAssistant = PaginationAssistant(aHundredCommits)
          val result = paginationAssistant.getCommitsForPageNumber(4)

          result.size shouldEqual 10
        }
      }
    }
  }

  private def generateCommits(howMany: Int): List[Commit] ={

    def loop(x: Int, state: List[Commit]): List[Commit] = x match {
      case 0 => state
      case _ => loop (x - 1, state :+ aCommit)
    }

    loop(howMany, List.empty[Commit])
  }
}
