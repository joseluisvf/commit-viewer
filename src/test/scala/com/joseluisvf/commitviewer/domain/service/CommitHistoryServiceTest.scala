package com.joseluisvf.commitviewer.domain.service

import com.joseluisvf.commitviewer.domain.commithistory.CommitRetrieverFallback
import com.joseluisvf.commitviewer.exception.ErrorInvalidUrl
import org.scalatest.{FunSuite, Matchers, WordSpec}

import scala.util.Failure

class CommitHistoryServiceTest extends WordSpec with Matchers {
  "A CommitHistoryService when retrieving commit history" when {
    "an invalid url is provided should fail with an ErrorInvalidUrl" in {
      val invalidUrl = "invalidurl"
      CommitHistoryService.getCommitHistoryOf(invalidUrl) shouldEqual Failure(ErrorInvalidUrl(invalidUrl))
    }
  }
}
