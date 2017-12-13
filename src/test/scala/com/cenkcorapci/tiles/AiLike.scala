package com.cenkcorapci.tiles

import com.cenkcorapci.tiles.ai.Heuristics._
import com.cenkcorapci.tiles.game.{GameState, TileGameAgainstAi}
import org.scalatest.{Matchers, WordSpec}

class AiLike extends WordSpec with Matchers {
  "Ai " should {
    (1 to 5).foreach { round =>
      s"be able to win against random in round $round" in {
        val isPlayerX = false // Random.nextBoolean

        def heuristic(gameState: GameState) = gameState.getBestMoveWithMinimaxWithAlphaBetaPruning(3, isPlayerX)

        def battleWithRandom(game: TileGameAgainstAi, player1: Boolean = true): Int =
          (if (player1) game.randomMove
          else game.counterWithAi) match {
            case Left(gameAgainstAi) =>
              gameAgainstAi.printGameSummary
              battleWithRandom(gameAgainstAi, !player1)
            case Right(result) => result
          }

        val size = 4
        val pieceCount = 3
        val initialState = GameState.createRandomState(size, pieceCount)
        println(s"statring with ${initialState.stateSummary}")
        val game = TileGameAgainstAi(initialState, 10, isPlayerX, heuristic)
        battleWithRandom(game) shouldBe 1
      }
    }
  }
}
