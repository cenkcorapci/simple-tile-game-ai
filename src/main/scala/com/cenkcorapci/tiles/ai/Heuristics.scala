package com.cenkcorapci.tiles.ai

import com.cenkcorapci.tiles.game.GameState
import AlphaBetaPruning.minimaxWithAlphaBetaPruning

object Heuristics {


  implicit class BestByMinimax(state: GameState) {
    def getBestMoveWithMinimax(searchDepth: Int): Option[GameState] = {
      def minimax(states: Seq[GameState], depth: Int = searchDepth, max: Boolean = true): Option[GameState] = {
        if (depth == 0) {
          if (max) states.sortBy(-_.stateScore).headOption
          else states.sortBy(_.stateScore).headOption
        }
        else {
          if (max) states.map(_.getNextStatesForPlayer1)
            .flatMap(s => minimax(s, depth - 1, !max))
            .sortBy(-_.stateScore)
          else states.map(_.getNextStatesForPlayer2)
            .flatMap(s => minimax(s, depth - 1, !max))
            .sortBy(_.stateScore)
        }.filterNot(_.board.sameElements(state.board)).headOption
      }

      minimax(state.getNextStatesForPlayer1)
    }

    def getBestMoveWithMinimaxWithAlphaBetaPruning(searchDepth: Int): Option[GameState] =
      state.getNextStatesForPlayer1
        .map(s => (minimaxWithAlphaBetaPruning(s, searchDepth), s))
        .sortBy(- _._1)
        .headOption
        .map(_._2)

  }

}
