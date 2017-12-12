package com.cenkcorapci.tiles.game

import scala.util.{Random, Try}

case class GameState(board: Array[Array[Int]],
                     p1PiecesCoordinateCache: Seq[(Int, Int)] = Seq.empty,
                     p2PiecesCoordinateCache: Seq[(Int, Int)] = Seq.empty) {
  private lazy val base = 'a'.toInt

  lazy val stateSummary: (Int, Int) = calculateStateSummary()

  def printState() =
    (("$" + (0 until board.head.length).map(i => s"  | ${i + 1}").mkString(""))
      +:
      board
        .map(_.map {
          case 1 => "X"
          case 2 => "O"
          case _ => " "
        }).zipWithIndex
        .map { case (arr, i) =>
          (base + i).toChar +: arr.map(s => s" | $s")
        }
        .map(_.mkString(" "))
        .map(row => s"${(0 until row.length).map(_ => "-").mkString("")}\n$row")

      )
      .foreach(println)

  def playersAvailableMoveCount(coordinateSeq: Seq[(Int, Int)]) = coordinateSeq
    .flatMap {
      case (row, column) =>
        val rows = (-1 to 1).map(i => row + i)
          .flatMap(r => Try(board(r)).toOption)

        rows.flatMap(arr => (-1 to 1).map(i => column + i).flatMap(c => Try(arr(c)).toOption))

    }
    .count(_ == 0)

  /**
    * Returns a tuple that shows (player ones available moves, player twos available moves)
    */
  private def calculateStateSummary() = (playersAvailableMoveCount(p1PiecesCoordinateCache), playersAvailableMoveCount(p2PiecesCoordinateCache))

  def getNextStatesForPlayer1() = getAvailableNextStates(true)

  def getNextStatesForPlayer2() = getAvailableNextStates(false)

  def move(player: Int, from: (Int, Int), to: (Int, Int)): Option[GameState] = {
    val (row, column) = from
    val (toRow, toColumn) = to
    if (Math.abs(column - toColumn) + Math.abs(row - toRow) > 1) // no diagonal moves are allowed
      None
    else if (board(row)(column) == player && board(toRow)(toColumn) == 0) {
      val b = board.updated(toRow, board(toRow).updated(toColumn, player))
      val newBoard = b.updated(row, b(row).updated(column, 0))
      Some(GameState.this.copy(board = newBoard))
        .map(s => if (player == 1) s.copy(p1PiecesCoordinateCache = s.p1PiecesCoordinateCache.filterNot(_ == from) ++ Seq(to))
        else if (player == 2) s.copy(p2PiecesCoordinateCache = s.p2PiecesCoordinateCache.filterNot(_ == from) ++ Seq(to))
        else s)
    } else None

  }


  private def getAvailableNextStates(isPlayer1: Boolean): Seq[GameState] = {
    /**
      *
      * @param coord
      * @return right if there is a desired state
      */
    def getNextStatesForPiece(coord: (Int, Int), index: Int, player1: Boolean): Seq[GameState] = {
      val (row, column) = coord
      val piece = board(row)(column)
      val vicinity = List((0, 1), (0, -1), (1, 0), (-1, 0))
        .flatMap { case (x, y) =>
          val (toX, toY) = (row + x, column + y)
          Try(board(toX)(toY)).toOption.map(_ => (toX, toY))
        }


      def move(to: (Int, Int)): Option[GameState] = {
        val (toRow, toColumn) = to

        if (board(toRow)(toColumn) == 0) {

          val newBoard = {
            val b = board.updated(toRow, board(toRow).updated(toColumn, piece))
            b.updated(row, b(row).updated(column, 0))
          }

          Some(GameState.this.copy(board = newBoard))
            .map(s =>
              if (player1) s.copy(p1PiecesCoordinateCache = s.p1PiecesCoordinateCache.patch(index, Nil, 1) ++ Seq((toRow, toColumn)))
              else s.copy(p2PiecesCoordinateCache = s.p2PiecesCoordinateCache.patch(index, Nil, 1) ++ Seq((toRow, toColumn)))
            )
        } else None
      }

      vicinity.flatMap(move)
    }

    if (isPlayer1) p1PiecesCoordinateCache.zipWithIndex
      .flatMap {
        case (coord, index) => getNextStatesForPiece(coord, index, isPlayer1)
      }
    else p2PiecesCoordinateCache.zipWithIndex
      .flatMap {
        case (coord, index) => getNextStatesForPiece(coord, index, isPlayer1)
      }

  }

}

object GameState {
  def createRandomState(size: Int, numOfPiecesPerPlayer: Int): GameState = {
    val partitioner = size * size

    def generatePlaces(acc: Set[Int] = Set.empty): Set[Int] =
      if (acc.size == numOfPiecesPerPlayer * 2) acc
      else generatePlaces(acc ++ Set(Random.nextInt(partitioner)))

    def placeThem(pieceSet: Map[Int, Int], index: Int = 0, array: Array[Int] = Array.empty): Array[Int] =
      if (array.length == partitioner) array
      else placeThem(pieceSet, index + 1, array :+ pieceSet.getOrElse(index, 0))

    val (p1Pieces, p2Pieces) = generatePlaces().splitAt(numOfPiecesPerPlayer)
    val pieces = p1Pieces.map(x => (x, 1)).toMap ++ p2Pieces.map(x => (x, 2)).toMap

    GameState(placeThem(pieces).grouped(size).toArray,
      p1Pieces.map(x => (x / size, x % size)).toSeq,
      p2Pieces.map(x => (x / size, x % size)).toSeq)
  }
}
