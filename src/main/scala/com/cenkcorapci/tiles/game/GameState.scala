package com.cenkcorapci.tiles.game

import scala.util.{Random, Try}

case class GameState(board: Array[Array[Int]],
                     xPiecesCoordinateCache: Seq[(Int, Int)] = Seq.empty,
                     oPiecesCoordinateCache: Seq[(Int, Int)] = Seq.empty,
                     commandFromPreviousState: Option[((Int, Int), (Int, Int))] = None) {
  private lazy val base = 'a'.toInt

  lazy val stateSummary: (Int, Int) = calculateStateSummary()

  lazy val stateScore: Int = stateSummary._1 - stateSummary._2

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
  private def calculateStateSummary() = (playersAvailableMoveCount(xPiecesCoordinateCache), playersAvailableMoveCount(oPiecesCoordinateCache))

  def getNextStatesForPlayerX() = getAvailableNextStates(true)

  def getNextStatesForPlayerO() = getAvailableNextStates(false)

  def move(player: Int, from: (Int, Int), to: (Int, Int)): Option[GameState] = {
    val (row, column) = from
    val (toRow, toColumn) = to
    val maybeCommand = Try((from, to)).toOption

    if (Math.abs(column - toColumn) + Math.abs(row - toRow) > 1) // no diagonal moves are allowed
      None
    else if (board(row)(column) == player && board(toRow)(toColumn) == 0) {
      val b = board.updated(toRow, board(toRow).updated(toColumn, player))
      val newBoard = b.updated(row, b(row).updated(column, 0))
      Some(GameState.this.copy(board = newBoard))
        .map(s => if (player == 1) s.copy(xPiecesCoordinateCache = s.xPiecesCoordinateCache.filterNot(_ == from) ++ Seq(to))
        else if (player == 2) s.copy(oPiecesCoordinateCache = s.oPiecesCoordinateCache.filterNot(_ == from) ++ Seq(to))
        else s)
        .map(_.copy(commandFromPreviousState = maybeCommand))
    } else None

  }


  private def getAvailableNextStates(isPlayerX: Boolean): Seq[GameState] = {
    /**
      * @param coord
      * @return right if there is a desired state
      */
    def getNextStatesForPiece(coord: (Int, Int), index: Int, playerX: Boolean): Seq[GameState] = {
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
          val someCommand4It = Some(((row, column), (toRow, toColumn)))
          Some(GameState.this.copy(board = newBoard))
            .map(_.copy(commandFromPreviousState = someCommand4It))
            .map(s =>
              if (playerX) s.copy(xPiecesCoordinateCache = s.xPiecesCoordinateCache.patch(index, Nil, 1) ++ Seq((toRow, toColumn)))
              else s.copy(oPiecesCoordinateCache = s.oPiecesCoordinateCache.patch(index, Nil, 1) ++ Seq((toRow, toColumn)))
            )
        } else None
      }

      vicinity.flatMap(move)
    }

    if (isPlayerX) xPiecesCoordinateCache.zipWithIndex
      .flatMap {
        case (coord, index) => getNextStatesForPiece(coord, index, isPlayerX)
      }
    else oPiecesCoordinateCache.zipWithIndex
      .flatMap {
        case (coord, index) => getNextStatesForPiece(coord, index, isPlayerX)
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
