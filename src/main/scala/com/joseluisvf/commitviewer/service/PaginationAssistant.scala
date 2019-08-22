package com.joseluisvf.commitviewer.service

import com.joseluisvf.commitviewer.domain.commit.Commit

/**
  * This assistant is responsible for helping out with the nuances of pagination for commit requests.
  *
  * It is tightly bound with the domain representation of commits.
  *
  * It provides reliability at the expense of being slightly nuanced in the following ways:
  * - if the amount of commits requested exceeds page capacity, then the last page of 10 commits is returned
  * - if commits per page exceeds the total of commits, then all of them are returned
  */
case class PaginationAssistant(commits: List[Commit], commitsPerPage: Int = PaginationAssistant.DEFAULT_AMOUNT_COMMITS_PER_PAGE) {
  def getCommitsForPageNumber(pageNumber: Int)
  : List[Commit] = {
    if (pageNumber < 0) {
      List.empty[Commit]
    } else {
      val pageNumberStartingAtZero = pageNumber - 1
      val commitsSize = commits.size
      val commitsOfDesiredPage = pageNumberStartingAtZero * commitsPerPage

      if (commitsPerPage > commitsSize) {
        commits
      } else {
        if (commitsOfDesiredPage > commitsSize) {
          val lastPageNumber = commitsSize / commitsPerPage
          getCommitsForPageNumber(lastPageNumber)
        } else {
          commits.slice(commitsOfDesiredPage, commitsOfDesiredPage + commitsPerPage)
        }
      }
    }
    }
}

object PaginationAssistant {
  val DEFAULT_AMOUNT_COMMITS_PER_PAGE = 30
}
