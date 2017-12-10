package com.cenkcorapci.tiles.game

import scala.util.Random

case class TileGameAgainstAi(boardSetting: GameState, moveLimit: Int, heuristic: (GameState) => Option[GameState]) {
  println(s"\n---Move left:$moveLimit ----")
  println(s"${boardSetting.stateSummary}")
  println(boardSetting.printState)

  /**
    * Lets user to move, makes a counter move with ai if plausible
    *
    * @param from
    * @param to
    * @return either a new games state or result which means draw if 0, win for player 1 if 1, win for player 2 if 2
    */
  def setMove(from: (Int, Int), to: (Int, Int)): Either[TileGameAgainstAi, Int] = {
    if (moveLimit > 0) {
      val player = boardSetting.move(2, from, to)
      if (moveLimit > 1) player match {
        case Some(move) => doCounterMove(move, moveLimit - 1)
        case None => Right(1)
      }
      else Right(endGame)
    }
    else Right(endGame)
  }

  def doRandomMove(): Either[TileGameAgainstAi, Int] = {
    if (moveLimit > 0) {
      val player = Random
        .shuffle(boardSetting
          .getNextStatesForPlayer2
          .filterNot(_.board.sameElements(boardSetting.board)))
        .headOption
      if (moveLimit > 1) player match {
        case Some(state) => doCounterMove(state, moveLimit - 1)
        case None => Right(1)
      }
      else Right(endGame)
    }
    else Right(endGame)
  }

  private def doCounterMove(newState: GameState, moveLeft: Int): Either[TileGameAgainstAi, Int] =
    if (moveLeft > 0) heuristic(newState) match {
      case Some(counterMove) => Left(TileGameAgainstAi(counterMove, moveLeft - 1, heuristic))
      case None => Right(2)
    }
    else Right(endGame)


  private def endGame() = {
    val (p1, p2) = boardSetting.stateSummary
    if (p1 > p2) 1
    else if (p1 < p2) 2
    else 0
  }
}
