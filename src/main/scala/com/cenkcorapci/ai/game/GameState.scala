package com.cenkcorapci.ai.game

import scala.util.{Random, Try}

case class GameState(board: Array[Array[Int]],
                     p1PiecesCoordinateCache: Set[(Int, Int)] = Set.empty,
                     p2PiecesCoordinateCache: Set[(Int, Int)] = Set.empty) {
  private lazy val base = 'a'.toInt

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
  def stateSummary() = (playersAvailableMoveCount(p1PiecesCoordinateCache), playersAvailableMoveCount(p2PiecesCoordinateCache))

  
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
