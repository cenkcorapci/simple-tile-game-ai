package com.cenkcorapci.tiles.utils

import scala.util.Try

object BoardConverters {

  implicit class TileConverter(tile: String) {
    private lazy val base = 'a'.toInt

    def tileToCoordinate(): Option[(Int, Int)] =
      if (tile.length >= 2) Try(
        ((tile.take(1).toLowerCase.head.toInt - base), tile.reverse.take(1).toInt - 1))
        .toOption
      else None
  }

  implicit class CoordinateConverter(coordinate: (Int, Int)) {
    private lazy val base = 'a'.toInt

    def coordinateToTile(): String = s"${(base + coordinate._1).toChar}${coordinate._2 + 1}"
  }

}


