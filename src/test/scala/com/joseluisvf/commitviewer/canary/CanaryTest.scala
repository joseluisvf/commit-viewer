package com.joseluisvf.commitviewer.canary

import org.scalatest.{Matchers, WordSpec}

class CanaryTest extends WordSpec with Matchers {
  "A canary" when {
    "asked to tweet" should {
      "return the standard tweet for any bird" in {
        val result = Canary.tweet
        val expected = "chirp"

        result shouldEqual expected
      }
    }
  }
}
