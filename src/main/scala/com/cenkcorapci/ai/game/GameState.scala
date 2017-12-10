package com.cenkcorapci.ai.game

import scala.util.Random

case class GameState(board: Array[Array[Int]]) {
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

    GameState(placeThem(pieces).grouped(size).toArray)
  }
}
