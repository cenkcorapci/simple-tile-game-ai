package com.cenkcorapci.tiles.game

import scala.util.Random

case class TileGameAgainstAi(boardSetting: GameState, moveLimit: Int, userIsX: Boolean = true, heuristic: (GameState) => Option[GameState]) {

  def printGameSummary() = {
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
    val movesAvailable = userIsX match {
      case true => boardSetting.getNextStatesForPlayerX.isEmpty
      case false => boardSetting.getNextStatesForPlayerO.isEmpty
    }
    if (movesAvailable)
      Right(1)
    else if (moveLimit > 0)
      boardSetting.move(if (userIsX) 1 else 2, from, to) match {
        case Some(newState) => Left(TileGameAgainstAi(newState, moveLimit - 1, userIsX, heuristic))
        case None => throw new Exception("Invalid move")
      }
    else Right(endGame)
  }

  def randomMove(): Either[TileGameAgainstAi, Int] = {
    if (moveLimit > 0) {
      val player = Random
        .shuffle(
          if (userIsX) boardSetting.getNextStatesForPlayerX
          else boardSetting.getNextStatesForPlayerO
            .filterNot(_.board.sameElements(boardSetting.board)))
        .headOption
      val next = player.flatMap(_.commandFromPreviousState)
        .flatMap { c =>
          val r = boardSetting.move(if (userIsX) 1 else 2, c._1, c._2)
          r
        }

      val dif = next.map(_.board.sameElements(boardSetting.board))
      dif.foreach(println)

      next match {
        case Some(newState) => Left(TileGameAgainstAi(newState, moveLimit - 1, userIsX, heuristic))
        case None => Right(1)
      }
    }
    else Right(endGame)
  }

  def counterWithAi(): Either[TileGameAgainstAi, Int] =
    if (moveLimit > 0) heuristic(boardSetting) match {
      case Some(counterMove) => Left(TileGameAgainstAi(counterMove, moveLimit - 1, userIsX, heuristic))
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
