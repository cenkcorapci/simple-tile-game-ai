package com.cenkcorapci.tiles.ai

import com.cenkcorapci.tiles.game.GameState

object Heuristics {


  implicit class BestByMinimax(state: GameState) {
    def getBestMoveWithMinimax(searchDepth: Int): Option[GameState] = {
      def minimax(states: Seq[GameState], depth: Int = searchDepth, max: Boolean = true): Option[GameState] = {
        if (depth == 0) {
          if (max) states.sortBy(-_.stateSummary.simpleScore).headOption
          else states.sortBy(_.stateSummary.simpleScore).headOption
        }
        else {
          if (max) states.map(_.getNextStatesForPlayer1)
            .flatMap(s => minimax(s, depth - 1, !max))
            .sortBy(-_.stateSummary.simpleScore)
          else states.map(_.getNextStatesForPlayer2)
            .flatMap(s => minimax(s, depth - 1, !max))
            .sortBy(_.stateSummary.simpleScore)
        }.filterNot(_.board.sameElements(state.board)).headOption
      }

      minimax(state.getNextStatesForPlayer1)
    }
  }

  private implicit class SimpleScoreFromTuple(t: (Int, Int)) {
    def simpleScore() = t._1 - t._2
  }

}
