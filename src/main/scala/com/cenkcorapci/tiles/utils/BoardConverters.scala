package com.cenkcorapci.tiles.utils

object BoardConverters {

  implicit class TileConverter(tile: String) {
    private lazy val base = 'a'.toInt

    def tileToCoordinate(): Either[(Int, Int), Throwable] =
      if (tile.length == 2) try {
        Left(((tile.take(1).toLowerCase.head.toInt - base), tile.reverse.take(1).toInt - 1))
      } catch {
        case exp: Throwable =>
          Right(exp)
      }
      else Right(new IndexOutOfBoundsException("Tile is not recognized"))
  }

  implicit class CoordinateConverter(coordinate: (Int, Int)) {
    private lazy val base = 'a'.toInt

    def coordinateToTile(): String = s"${(base + coordinate._1).toChar}${coordinate._2 + 1}"
  }

}
