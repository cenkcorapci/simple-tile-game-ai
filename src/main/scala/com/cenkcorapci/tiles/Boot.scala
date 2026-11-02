package com.cenkcorapci.tiles

import com.cenkcorapci.tiles.ai.Heuristics._
import com.cenkcorapci.tiles.game.{GameState, TileGameAgainstAi}
import com.cenkcorapci.tiles.utils.BoardConverters._

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

object Boot {

  private val Banner =
    """  _______ __        ______
      | /_  __(_) /__     / ____/___ _____ ___  ___
      |  / / / / / _ \   / / __/ __ `/ __ `__ \/ _ \
      | / / / / /  __/  / /_/ / /_/ / / / / / /  __/
      |/_/ /_/_/\___/   \____/\__,_/_/ /_/ /_/\___/
      |""".stripMargin

  private val Help = Seq(
    "- Type help for commands",
    "- Type quit for quitting the application",
    "- Type start to quick start a game against ai.",
    "-\t You can set piece count with -p flag. Example; 'start -p:7' for 7 pieces",
    "-\t You can set board size with -b flag. Example; 'start -b:6' for a 6x6 board",
    "-\t You can set move limit with -m flag. Example; 'start -m:50' for 50 moves",
    "-\t You can set difficulty with -d flag (must be greater than 0). Example; 'start -d:5' for a search with 5 step depth",
    "- Type 'to' between your coordinates to move. Example; typing 'a1 to a2' will move your piece if it's on a1 and if a2 is available."
  )

  private def flag(command: String, name: Char, default: Int): Int =
    s"-$name:([0-9]+)".r
      .findFirstMatchIn(command)
      .flatMap(_.group(1).toIntOption)
      .getOrElse(default)

  private def startGame(command: String): Try[TileGameAgainstAi] = Try {
    val boardSize = flag(command, 'b', 7)
    val pieceCount = flag(command, 'p', 6)
    val moveLimit = flag(command, 'm', 50)
    val searchDepth = flag(command, 'd', 2)
    require(searchDepth > 0, "Difficulty must be greater than 0")

    val newGame = GameState.createRandomState(boardSize, pieceCount)
    println(s"Move limit: $moveLimit")
    println("You are 'O'")
    newGame.printState()

    TileGameAgainstAi(newGame, moveLimit, _.getBestMoveWithMinimax(searchDepth))
  }

  private def describe(result: Int): String = result match {
    case 0 => "Draw."
    case 1 => "Ai wins."
    case _ => "You win."
  }

  /** Plays the human move then the AI reply; Left is the continuing game, Right the final result. */
  private def turn(game: TileGameAgainstAi,
                   from: (Int, Int),
                   to: (Int, Int)): Try[Either[TileGameAgainstAi, Int]] =
    Try(game.move(from, to)).map {
      case Right(result) => Right(result)
      case Left(afterPlayer) =>
        afterPlayer.printGameSummary()
        afterPlayer.counterWithAi.left.map { afterAi =>
          afterAi.printGameSummary()
          afterAi
        }
    }

  @tailrec
  private def loop(game: Option[TileGameAgainstAi]): Unit =
    Option(readLine()).map(_.trim.toLowerCase) match {
      case None | Some("quit") => ()
      case Some("help") =>
        Help.foreach(println)
        loop(game)
      case Some(s) if s.startsWith("start") =>
        startGame(s) match {
          case Success(g) => loop(Some(g))
          case Failure(t) =>
            println(s"Could not start game: ${t.getMessage}")
            loop(game)
        }
      case Some(s) if s.contains(" to ") =>
        val moved = for {
          g <- game
          (fromTile, toTile) <- s.split(" to ") match {
            case Array(a, b) => Some((a, b))
            case _           => None
          }
          from <- fromTile.tileToCoordinate
          to <- toTile.tileToCoordinate
        } yield turn(g, from, to)

        moved match {
          case Some(Success(Left(next))) => loop(Some(next))
          case Some(Success(Right(result))) => println(describe(result))
          case Some(Failure(t)) =>
            println(s"Invalid move. ${t.getMessage}")
            loop(game)
          case None =>
            println("Unknown move...")
            loop(game)
        }
      case Some(_) =>
        println("Unknown command...")
        loop(game)
    }

  def main(args: Array[String]): Unit = {
    println(Banner)
    println("- Type help for commands")
    println("- Type quit for quitting the application")
    loop(None)
  }
}
