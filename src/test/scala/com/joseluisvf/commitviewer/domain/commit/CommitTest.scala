package com.joseluisvf.commitviewer.domain.commit

import org.scalatest.{Matchers, WordSpec}

class CommitTest extends WordSpec with Matchers {
  "A commit when it is created" when {
      val hash = "4dc9442afcc350af040b3e8488c123394985750c"
      val subject = "Lorem Ipsum Bacon"
      val validSeparator = Commit.placeholderSeparator
      val validCommitAsText = s"$hash$validSeparator$subject"

      "its textual representation contains the expected number of fields" when {
        "the correct separator is used" when {
          "the correct amount of fields are present should be created successfully" in {
            val commit = Commit(validCommitAsText)
            commit.getHash shouldEqual hash
            commit.getSubject shouldEqual subject
          }
        }
        "the incorrect separator is used should throw an exception" in {
          val invalidSeparator = s"----------"
          val commitWithInvalidSeparatorAsText = s"$hash$invalidSeparator$subject"

          assertThrows[IllegalArgumentException] {
            Commit(commitWithInvalidSeparatorAsText)
          }
        }
      }

    "its textual representation does not contain the expected number of fields" when {
      "the textual representation is empty should throw an exception" in {
        assertThrows[IllegalArgumentException] {
          Commit(s"abc$validSeparator")
        }
      }

      "extraneous fields are present should throw an exception" in {
        assertThrows[IllegalArgumentException] {
          Commit(validCommitAsText + validSeparator + "extraneous field")
        }
      }

      "not enough fields are present should throw an exception" in {
        assertThrows[IllegalArgumentException] {
          Commit(s"abc$validSeparator")
        }
      }
    }
  }
}
