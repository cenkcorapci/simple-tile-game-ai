package com.cenkcorapci.tiles.ai

import com.cenkcorapci.tiles.game.GameState

object AlphaBetaPruning {
  def minimaxWithAlphaBetaPruning(state: GameState, maxDepth: Int): Int = {
    minimize(state, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE)
  }

  private def minimize(state: GameState,
                       depth: Int,
                       alpha: Int,
                       beta: Int): Int =
    if (depth == 0 || state.playersAvailableMoveCount(
          state.p1PiecesCoordinateCache) == 0)
      state.stateScore
    else {
      def loop(nextStates: Seq[GameState], newBeta: Int): Int =
        if (nextStates.isEmpty) newBeta
        else {
          val b =
            math.min(beta, maximize(nextStates.head, depth - 1, alpha, newBeta))
          if (alpha >= b) alpha
          else loop(nextStates.tail, b)
        }

      loop(state.getNextStatesForPlayer1, beta)
    }

  private def maximize(state: GameState,
                       depth: Int,
                       alpha: Int,
                       beta: Int): Int =
    if (depth == 0 || state.playersAvailableMoveCount(
          state.p1PiecesCoordinateCache) == 0)
      state.stateScore
    else {
      def loop(nextStates: Seq[GameState], newAlpha: Int): Int =
        if (nextStates.isEmpty) newAlpha
        else {
          val a = math.max(newAlpha,
                           minimize(nextStates.head, depth - 1, newAlpha, beta))
          if (a >= beta) beta
          else loop(nextStates.tail, a)
        }

      loop(state.getNextStatesForPlayer2, alpha)
    }

}
