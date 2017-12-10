package com.cenkcorapci.ai.game

import scala.util.{Random, Try}

case class GameState(board: Array[Array[Int]],
                     p1PiecesCoordinateCache: Set[(Int, Int)] = Set.empty,
                     p2PiecesCoordinateCache: Set[(Int, Int)] = Set.empty) {
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

  def playersAvailableMoveCount(coordinateSet: Set[(Int, Int)]) = coordinateSet.flatMap {
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

  def getNextStatesForPlayer1() = getAvailableNextStates(p1PiecesCoordinateCache)

  def getNextStatesForPlayer2() = getAvailableNextStates(p2PiecesCoordinateCache)

  def move(player: Int, from: (Int, Int), to: (Int, Int)) = {
    val (row, column) = from
    val (toRow, toColumn) = to
    if (board(row)(column) == player && board(toRow)(toColumn) == 0) {
      val newBoard = board.updated(toRow, board(toRow).updated(toColumn, board(row)(column)))
        .updated(row, board(toRow).updated(column, 0))
      Some(GameState.this.copy(board = newBoard))
    } else None

  }


  private def getAvailableNextStates(pieceCoordSet: Set[(Int, Int)]): Seq[GameState] = {
    /**
      *
      * @param coord
      * @return right if there is a desired state
      */
    def getNextStatesForPiece(coord: (Int, Int)): Seq[GameState] = {
      val (row, column) = coord
      val piece = board(row)(column)
      val vicinity = (-1 until 1).map(i => row + i)
        .flatMap(r => Try(board(r)).toOption.map(a => (r, a)))
        .flatMap { case (rowIndex, rowDef) =>
          (-1 until 1).map(i => column + i)
            .flatMap(c => Try(rowDef(c)).toOption.map(_ => (rowIndex, c)))
        }

      def find(to: (Int, Int)): Option[GameState] = {
        val (toRow, toColumn) = to
        if (board(toRow)(toColumn) == 0) {
          val newBoard = board.updated(toRow, board(toRow).updated(toColumn, piece))
            .updated(row, board(toRow).updated(column, 0))
          Some(GameState.this.copy(board = newBoard))
        } else None
      }

      vicinity.flatMap(find)
    }

    pieceCoordSet.toSeq.flatMap(getNextStatesForPiece)
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
      else placeThem(pieceSet, index + 1, array :+ pieceSet.get(index).getOrElse(0))

    val (p1Pieces, p2Pieces) = generatePlaces().splitAt(numOfPiecesPerPlayer)
    val pieces = p1Pieces.map(x => (x, 1)).toMap ++ p2Pieces.map(x => (x, 2)).toMap

    GameState(placeThem(pieces).grouped(size).toArray,
      p1Pieces.map(x => (x / size, x % size)),
      p2Pieces.map(x => (x / size, x % size)))
  }
}
