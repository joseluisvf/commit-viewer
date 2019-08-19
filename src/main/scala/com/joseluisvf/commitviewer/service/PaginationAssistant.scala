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
case class PaginationAssistant(commits: List[Commit], commitsPerPage: Int = 10) {
  def getResultsFor(pageNumber: Int)
  : List[Commit] = {
    val pageNumberStartingAtZero = pageNumber - 1
    val commitsSize = commits.size
    val resultsOfDesiredPage = pageNumberStartingAtZero * commitsPerPage

    if (commitsPerPage > commitsSize) {
      commits
    } else {
      if (resultsOfDesiredPage > commitsSize) {
        val lastPageNumber = commitsSize / commitsPerPage
        getResultsFor(lastPageNumber)
      } else {
        commits.slice(resultsOfDesiredPage, resultsOfDesiredPage + commitsPerPage)
      }
    }
  }
}
