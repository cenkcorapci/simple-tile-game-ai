package com.cenkcorapci.ai

import org.scalatest.{Matchers, WordSpec}
import com.cenkcorapci.ai.utils.BoardConverters._

class UtilitiesLike extends WordSpec with Matchers {

  "Tile converter" should {

    "convert tiles to coordinates" in {
      "a1".tileToCoordinate shouldBe Left((0, 0))
    }

    "fail to convert random strings to coordinates" in {
      "sdsad".tileToCoordinate shouldBe an[Right[(Int, Int), Throwable]]
    }

  }

  "Coordinate to Tile converter" should {

    "convert coordinates to tiles" in {
      (0, 0).coordinateToTile shouldBe "a1"
    }

  }
}
