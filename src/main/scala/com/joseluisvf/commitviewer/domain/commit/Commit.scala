package com.joseluisvf.commitviewer.domain.commit

import com.joseluisvf.commitviewer.exception.{ErrorEmptyCommitTextualRepresentation, ErrorMalformedCommitTextualRepresentation}

import scala.util.Failure

/**
  * Responsible for containing the required information for a given commit.
  *
  * Part of its contract is to offer a way to be created from a textual representation of a commit.
  *
  * @param hash    the hash of the commit
  * @param subject the subject of the commit
  */
class Commit(private val hash: String, private val subject: String) {
  def getHash: String = hash

  def getSubject: String = subject

  override def toString: String = s"$hash $getSubject"
}

object Commit {
  val placeholderSeparator: String = ">>>>>>>>>>>>>>>>>>>>>>>>>>>>"

  def apply(commitAsText: String): Commit = {
    require(commitAsText != "", Failure(ErrorEmptyCommitTextualRepresentation()))

    val split = commitAsText.split(placeholderSeparator)
    require(split.size == 2, Failure(ErrorMalformedCommitTextualRepresentation(commitAsText)))

    new Commit(split(0), split(1))
  }
}

