package com.joseluisvf.commitviewer.domain.commit

import com.joseluisvf.commitviewer.domain.commithistory.CommitRetrieverFallback.CommitsAsText

object Commits {
  def fromTextToCommits(commitsAsText: CommitsAsText): List[Commit] = {
    commitsAsText.map(Commit(_))
  }

  def fromCommitsToText(commits: List[Commit]): String = {
    commits.mkString("\n")
  }
}
