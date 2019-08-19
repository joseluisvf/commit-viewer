package com.joseluisvf.commitviewer.common

import java.nio.file.Paths

object CommitViewerCommon {
  val targetDir: String = Paths.get("src/main/resources/commit_history_result").toAbsolutePath.toString
  val targetFileName: String = "commit-history.txt"
  val httpEndpointPortNumber: Int = 12345
  val DEFAULT_TIMEOUT_MILLISECONDS: Long = 30 * 1000
}
