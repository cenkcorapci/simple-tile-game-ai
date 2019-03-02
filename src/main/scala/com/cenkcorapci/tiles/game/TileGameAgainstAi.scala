package com.cenkcorapci.tiles.game

import scala.util.Random

case class TileGameAgainstAi(boardSetting: GameState,
                             moveLimit: Int,
                             heuristic: (GameState) => Option[GameState]) {

  def printGameSummary(): Unit = {
    println(s"\n---Move left:$moveLimit ----")
    println(s"${boardSetting.stateSummary}")
    println(boardSetting.printState)

  }

  /**
    * Lets user to move, makes a counter move with ai if plausible
    *
    * @param from
    * @param to
    * @throws Exception
    * @return either a new games state or result which means draw if 0, win for player 1 if 1, win for player 2 if 2
    */
  def move(from: (Int, Int), to: (Int, Int)): Either[TileGameAgainstAi, Int] = {
    if (boardSetting.getNextStatesForPlayer2.isEmpty)
      Right(1)
    else if (moveLimit > 0)
      boardSetting.move(2, from, to) match {
        case Some(newState) =>
          Left(TileGameAgainstAi(newState, moveLimit - 1, heuristic))
        case None => throw new Exception("Invalid move")
      } else Right(endGame)
  }

  def randomMove: Either[TileGameAgainstAi, Int] = {
    if (moveLimit > 0) {
      val player = Random
        .shuffle(
          boardSetting.getNextStatesForPlayer2
            .filterNot(_.board.sameElements(boardSetting.board)))
        .headOption
      if (moveLimit > 1) player match {
        case Some(newState) =>
          Left(TileGameAgainstAi(newState, moveLimit - 1, heuristic))
        case None => Right(1)
      } else Right(endGame)
    } else Right(endGame)
  }

  def counterWithAi: Either[TileGameAgainstAi, Int] =
    if (moveLimit > 0) heuristic(boardSetting) match {
      case Some(counterMove) =>
        Left(TileGameAgainstAi(counterMove, moveLimit - 1, heuristic))
      case None => Right(2)
    } else Right(endGame)

  private def endGame: Int = {
    val (p1, p2) = boardSetting.stateSummary
    if (p1 > p2) 1
    else if (p1 < p2) 2
    else 0
  }
}
