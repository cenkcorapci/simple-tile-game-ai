package com.cenkcorapci.tiles.ai

import com.cenkcorapci.tiles.game.GameState
import AlphaBetaPruning.minimaxWithAlphaBetaPruning

object Heuristics {


  implicit class BestByMinimax(state: GameState) {
    /**
      * Minimax without pruning, only works if ai is X
      *
      * @deprecated
      * @param searchDepth
      * @return
      */
    def getBestMoveWithMinimax(searchDepth: Int): Option[GameState] = {
      def minimax(states: Seq[GameState], depth: Int = searchDepth, max: Boolean = true): Option[GameState] = {
        if (depth == 0) {
          if (max) states.sortBy(-_.stateScore).headOption
          else states.sortBy(_.stateScore).headOption
        }
        else {
          if (max) states.map(_.getNextStatesForPlayerX)
            .flatMap(s => minimax(s, depth - 1, !max))
            .sortBy(-_.stateScore)
          else states.map(_.getNextStatesForPlayerO)
            .flatMap(s => minimax(s, depth - 1, !max))
            .sortBy(_.stateScore)
        }.filterNot(_.board.sameElements(state.board)).headOption
      }

      minimax(state.getNextStatesForPlayerX)
    }

    def getBestMoveWithMinimaxWithAlphaBetaPruning(searchDepth: Int, isPlayerX: Boolean): Option[GameState] =
      if (isPlayerX)
        state.getNextStatesForPlayerX
          .map(s => (minimaxWithAlphaBetaPruning(s, searchDepth, isPlayerX), s))
          .sortBy(-_._1)
          .headOption
          .map(_._2)
      else
        state.getNextStatesForPlayerO
          .map(s => (minimaxWithAlphaBetaPruning(s, searchDepth, isPlayerX), s))
          .sortBy(-_._1)
          .headOption
          .map(_._2)

  }

}
