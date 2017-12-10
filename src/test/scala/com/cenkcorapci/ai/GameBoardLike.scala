package com.cenkcorapci.ai

import org.scalatest.{Matchers, WordSpec}
import com.cenkcorapci.ai.game.GameState

class GameBoardLike extends WordSpec with Matchers {
  "Game State " should {
    val boardSize = 7
    val numOfPieces = 8
    "be able to be randomly generated" in {
      val state = GameState.createRandomState(boardSize, numOfPieces)
      state.printState()
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
    }
  }
}
