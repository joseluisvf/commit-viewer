package com.joseluisvf.commitviewer.domain.commithistory

import com.joseluisvf.commitviewer.domain.commit.Commit
import com.joseluisvf.commitviewer.exception.ErrorRepositoryNotFoundFor
import org.apache.logging.log4j.scala.Logging

import scala.io.Source
import scala.sys.process._
import scala.util.{Failure, Success, Try}

/**
  * Responsible for invoking the fallback script for commit retrieval.
  */
object CommitRetrieverFallback extends Logging {
  val SUCCESS_ERROR_CODE = 0
  val REPOSITORY_NOT_FOUND_ERROR_CODE = 2
  type CommitsAsText = List[String]

  def getCommitHistoryOf(githubRepositoryUrl: String, targetDir: String, targetFileName: String)
  : Try[CommitsAsText] = {
    createCommitHistoryFile(githubRepositoryUrl, targetDir, targetFileName) match {
      case Failure(e) => Failure(e)
      case Success(_) =>
        val commitsAsText
        = Source
          .fromFile(s"$targetDir/$targetFileName")
          .getLines()
          .toList

        Success(commitsAsText)
    }
  }

  private def createCommitHistoryFile(githubRepositoryUrl: String, targetDir: String, commitHistoryFileName: String)
  : Try[Unit] = {
    val command
    = s"${System.getProperty("user.dir")}/src/main/resources/commit_history_result/create-file-with-commit-history.sh" +
      s" ${githubRepositoryUrl.withoutTrailingForwardSlash()}" +
      s" $targetDir" +
      s" $commitHistoryFileName" +
      s" ${Commit.placeholderSeparator}"

    logger.debug(s"Running the following command: <$command>")
    val fallbackScriptReturnCode = command.!

    if (fallbackScriptReturnCode == REPOSITORY_NOT_FOUND_ERROR_CODE) {
      Failure(ErrorRepositoryNotFoundFor(githubRepositoryUrl))
    } else {
      require(fallbackScriptReturnCode == SUCCESS_ERROR_CODE)
      Success()
    }
  }

  private implicit class UrlSafeguard(s: String) {
    def withoutTrailingForwardSlash(): String = {
      if (s.charAt(s.length - 1) == '/') {
        s.substring(0, s.length - 1)
      } else {
        s
      }
    }
  }

}
