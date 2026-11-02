package com.cenkcorapci.tiles.utils

object BoardConverters {

  private val Base = 'a'.toInt
  private val TilePattern = "([a-zA-Z])([0-9]+)".r

  implicit class TileConverter(tile: String) {
    def tileToCoordinate: Option[(Int, Int)] =
      tile.trim match {
        case TilePattern(letter, number) =>
          number.toIntOption
            .filter(_ >= 1)
            .map(n => (letter.head.toLower.toInt - Base, n - 1))
        case _ => None
      }
  }

  implicit class CoordinateConverter(coordinate: (Int, Int)) {
    def coordinateToTile: String =
      s"${(Base + coordinate._1).toChar}${coordinate._2 + 1}"
  }

}
