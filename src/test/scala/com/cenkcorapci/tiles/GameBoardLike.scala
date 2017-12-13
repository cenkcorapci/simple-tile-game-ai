package com.cenkcorapci.tiles

import com.cenkcorapci.tiles.game.GameState
import org.scalatest.{Matchers, WordSpec}

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

      state.xPiecesCoordinateCache.size shouldBe numOfPieces
      state.oPiecesCoordinateCache.size shouldBe numOfPieces
    }
    "should create a state that makes sense" in {
      val (p1, p2) = state.stateSummary
      p1 + p2 > 0 shouldBe true
    }
    "should be able to have available moves" in {
      state.getNextStatesForPlayerX.size + state.getNextStatesForPlayerO.size > 0 shouldBe true
    }
    "should be able to transition to new states" in {
      val initialBoard = Array(Array(1, 0), Array(0, 0))
      val desiredBoard = Array(Array[Int](0, 0), Array[Int](1, 0))
      val predefState = GameState(initialBoard)
      predefState.move(1, (0, 0), (1, 0)).map(_.board).getOrElse(Array.empty) shouldBe desiredBoard
      predefState.move(1, (1, 1), (1, 0)) shouldBe None
    }
  }

}
