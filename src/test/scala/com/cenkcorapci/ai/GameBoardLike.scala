package com.cenkcorapci.ai

import org.scalatest.{Matchers, WordSpec}
import com.cenkcorapci.ai.game.GameState

class GameBoardLike extends WordSpec with Matchers {
  "Game State " should {
    val boardSize = 7
    val numOfPieces = 8

    val state = GameState.createRandomState(boardSize, numOfPieces)

    "be able to be randomly generated" in {
      state.board.size shouldBe boardSize
      state.board.head.size shouldBe boardSize

      val pieces: (Int, Int) = state.board
        .flatten
        .foldLeft((0, 0))((acc, x) =>
          if (x == 1) (acc._1 + 1, acc._2)
          else if (x == 2) (acc._1, acc._2 + 1)
          else (acc._1, acc._2)
        )

      pieces shouldBe Tuple2(numOfPieces, numOfPieces)

      state.p1PiecesCoordinateCache.size shouldBe numOfPieces
      state.p2PiecesCoordinateCache.size shouldBe numOfPieces
    }
    " should create a state that makes sense" in {
      val (p1, p2) = state.stateSummary
      p1 + p2 > 0 shouldBe true
    }
  }

}
