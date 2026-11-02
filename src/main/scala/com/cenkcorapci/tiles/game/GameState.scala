package com.cenkcorapci.tiles.game

import scala.util.Random

final case class GameState(board: Array[Array[Int]],
                           p1PiecesCoordinateCache: Seq[(Int, Int)] = Seq.empty,
                           p2PiecesCoordinateCache: Seq[(Int, Int)] = Seq.empty) {

  lazy val stateSummary: (Int, Int) =
    (playersAvailableMoveCount(p1PiecesCoordinateCache),
     playersAvailableMoveCount(p2PiecesCoordinateCache))

  lazy val stateScore: Int = stateSummary._1 - stateSummary._2

  private def inBounds(row: Int, column: Int): Boolean =
    row >= 0 && row < board.length && column >= 0 && column < board(row).length

  def render: String = {
    val header = "$" + board.indices.map(i => s"  | ${i + 1}").mkString
    val rows = board.zipWithIndex.map {
      case (cells, i) =>
        val symbols = cells.map {
          case 1 => "X"
          case 2 => "O"
          case _ => " "
        }
        ((GameState.Base + i).toChar.toString +: symbols.map(s => s" | $s")).mkString(" ")
    }
    (header +: rows.flatMap(row => Seq("-" * row.length, row))).mkString("\n")
  }

  def printState(): Unit = println(render)

  /** Counts the empty cells in the 3x3 neighbourhood of each given piece. */
  def playersAvailableMoveCount(coordinateSeq: Seq[(Int, Int)]): Int =
    (for {
      (row, column) <- coordinateSeq
      r <- row - 1 to row + 1
      c <- column - 1 to column + 1
      if inBounds(r, c) && board(r)(c) == 0
    } yield 1).sum

  def getNextStatesForPlayer1: Seq[GameState] = nextStates(player = 1)

  def getNextStatesForPlayer2: Seq[GameState] = nextStates(player = 2)

  private def withPieces(player: Int,
                         f: Seq[(Int, Int)] => Seq[(Int, Int)]): GameState =
    player match {
      case 1 => copy(p1PiecesCoordinateCache = f(p1PiecesCoordinateCache))
      case 2 => copy(p2PiecesCoordinateCache = f(p2PiecesCoordinateCache))
      case _ => this
    }

  /** Moves `player`'s piece one step orthogonally; None if the move is illegal. */
  def move(player: Int, from: (Int, Int), to: (Int, Int)): Option[GameState] = {
    val (row, column) = from
    val (toRow, toColumn) = to
    val isStep = Math.abs(column - toColumn) + Math.abs(row - toRow) == 1

    if (isStep && inBounds(row, column) && inBounds(toRow, toColumn) &&
        board(row)(column) == player && board(toRow)(toColumn) == 0) {
      val newBoard = board.updated(toRow, board(toRow).updated(toColumn, player))
      val cleared = newBoard.updated(row, newBoard(row).updated(column, 0))
      Some(
        copy(board = cleared)
          .withPieces(player, _.filterNot(_ == from) :+ to))
    } else None
  }

  private def nextStates(player: Int): Seq[GameState] = {
    val pieces =
      if (player == 1) p1PiecesCoordinateCache else p2PiecesCoordinateCache
    for {
      from @ (row, column) <- pieces
      (dr, dc) <- GameState.Directions
      state <- move(player, from, (row + dr, column + dc))
    } yield state
  }
}

object GameState {
  private val Base = 'a'.toInt
  private val Directions = Seq((0, 1), (0, -1), (1, 0), (-1, 0))

  def createRandomState(size: Int, numOfPiecesPerPlayer: Int): GameState = {
    require(size > 0, "Board size must be positive")
    require(numOfPiecesPerPlayer > 0 && numOfPiecesPerPlayer * 2 <= size * size,
            "Board is too small for that many pieces")

    val (p1Places, p2Places) = Random
      .shuffle((0 until size * size).toVector)
      .take(numOfPiecesPerPlayer * 2)
      .splitAt(numOfPiecesPerPlayer)

    val owners = p1Places.map(_ -> 1).toMap ++ p2Places.map(_ -> 2).toMap
    val board = Array.tabulate(size, size)((r, c) =>
      owners.getOrElse(r * size + c, 0))

    def coordinates(places: Seq[Int]) = places.map(x => (x / size, x % size))

    GameState(board, coordinates(p1Places), coordinates(p2Places))
  }
}
