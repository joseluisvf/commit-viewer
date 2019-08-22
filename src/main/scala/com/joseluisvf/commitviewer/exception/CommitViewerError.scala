package com.joseluisvf.commitviewer.exception

import com.joseluisvf.commitviewer.domain.commit.Commit

sealed trait CommitViewerError extends Throwable {
  val errorMessage: String
  override def getMessage: String = errorMessage
}

case class ErrorRepositoryNotFoundFor(repositoryUrl: String) extends CommitViewerError {
  override val errorMessage: String = s"Unable to clone the provided repository url:<$repositoryUrl>"
}

case class ErrorInvalidUrl(invalidUrl: String) extends CommitViewerError {
  override val errorMessage: String = s"The url provided <$invalidUrl> is not valid. Keep in mind it must begin with https://"
}

case class ErrorEmptyCommitTextualRepresentation() extends CommitViewerError {
  override val errorMessage: String = s"Unable to create a commit from an empty string."
}

case class ErrorBadGithubCredentials() extends CommitViewerError {
  override val errorMessage: String = s"The credentials provided are deemed invalid for interaction with Github's API."
}

case class ErrorMalformedCommitTextualRepresentation(malformedCommitTextualRepresentation: String) extends CommitViewerError {
  override val errorMessage: String =
    s"""
       |The textual representation of a commit must contain precisely two fields separated by ${Commit.placeholderSeparator}.
       | In that sense, the following is invalid: <$malformedCommitTextualRepresentation>
      """.stripMargin
}

case class ErrorInsufficientArguments() extends CommitViewerError {
  override val errorMessage: String =
    """Usage:
               githubRepositoryUrl (REQUIRED): an url to an existing public github repository
    """.stripMargin
}

case class ErrorTimeout(timeoutDurationMilliseconds: Long) extends CommitViewerError {
  override val errorMessage: String = s"Request timeout. The required operation exceeded the maximum amount of $timeoutDurationMilliseconds milliseconds."
}
