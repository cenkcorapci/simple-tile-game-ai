package com.cenkcorapci.tiles

import com.cenkcorapci.tiles.utils.BoardConverters._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UtilitiesLike extends AnyWordSpec with Matchers {

  "Tile converter" should {

    "convert tiles to coordinates" in {
      "a1".tileToCoordinate shouldBe Some((0, 0))
    }

    "fail to convert random strings to coordinates" in {
      "sdsad".tileToCoordinate shouldBe None
    }

  }

  "Coordinate to Tile converter" should {

    "convert coordinates to tiles" in {
      (0, 0).coordinateToTile shouldBe "a1"
    }

  }
}
