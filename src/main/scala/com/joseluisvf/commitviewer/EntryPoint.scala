package com.joseluisvf.commitviewer

import java.nio.file.Paths

import com.joseluisvf.commitviewer.domain.service.CommitHistoryService
import com.joseluisvf.commitviewer.exception.ErrorInsufficientArguments
import org.apache.logging.log4j.scala.Logging

object EntryPoint extends App with Logging {
  require(args.length == 1, ErrorInsufficientArguments())
  val githubRepositoryUrl = args(0)
  val res = CommitHistoryService.getCommitHistoryOf(githubRepositoryUrl)
  logger.info(res)
}
