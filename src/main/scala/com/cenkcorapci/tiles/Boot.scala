package com.cenkcorapci.tiles

import com.cenkcorapci.tiles.game.{GameState, TileGameAgainstAi}

import io.StdIn.readLine
import scala.util.{Failure, Success, Try}
import com.cenkcorapci.tiles.ai.Heuristics._
import com.cenkcorapci.tiles.utils.BoardConverters._

object Boot extends App {
  println("  _______ __        ______                   \n /_  __(_) /__     / ____/___ _____ ___  ___ \n  / / / / / _ \\   / / __/ __ `/ __ `__ \\/ _ \\\n / / / / /  __/  / /_/ / /_/ / / / / / /  __/\n/_/ /_/_/\\___/   \\____/\\__,_/_/ /_/ /_/\\___/ \n                                             ")
  println
  println("- Type help for commands")
  println("- Type quit for quitting the application")

  def heuristic(gameState: GameState) = gameState.getBestMoveWithMinimax(2)

  def endGame(result: Int): Unit = {
    result match {
      case 0 => println("Draw.")
      case 1 => println("Ai wins.")
      case 2 => println("You win.")
      case _ => System.exit(0)
    }
    System.exit(0)
  }

  def play(tileGameAgainstAi: Option[TileGameAgainstAi] = None): Unit = {
    val in = readLine
    in.toLowerCase match {
      case "quit" =>
        System.exit(0)
      case "help" =>
        println("- Type help for commands")
        println("- Type quit for quitting the application")
        println("- Type start to quick start a game against ai.")
        println("-\t You can set piece count with -p flag.Example; 'start -p:7' for 7 pieces")
        println("-\t You can set board size with -b flag.Example; 'start -b:6' for a 6x6 board")
        println("-\t You can set move limit with -m flag.Example; 'start -m:50' for a 50 moves")
        println("- Type 'to' between your coordinates to move.Example; typing 'a1 to a2' will your piece if it's on a1 and if a2 is available.")
        play(tileGameAgainstAi)
      case s if s.startsWith("start") =>
        Try {
          val boardSize = "-b:[0-9]+".r.findFirstMatchIn(s).flatMap(_.toString.split(":").lastOption).map(_.toInt).getOrElse(7)
          val pieceCount = "-p:[0-9]+".r.findFirstMatchIn(s).flatMap(_.toString.split(":").lastOption).map(_.toInt).getOrElse(6)
          val moveLimit = "-m:[0-9]+".r.findFirstMatchIn(s).flatMap(_.toString.split(":").lastOption).map(_.toInt).getOrElse(50)
          val newGame = GameState.createRandomState(boardSize, pieceCount)
          println(s"Move limit: $moveLimit")
          println("You are 'O'")
          newGame.printState
          TileGameAgainstAi(newGame, moveLimit, heuristic)
        } match {
          case Success(g) => play(Option(g))
          case Failure(t) => println(s"Unknown command.${t.getMessage}")
        }
      case s if s.contains(" to ") =>
        s.split(" to ") match {
          case Array(from, to) =>
            tileGameAgainstAi match {
              case Some(gameAgainstAi) =>
                for {
                  f <- from.tileToCoordinate
                  t <- to.tileToCoordinate
                } yield Try(gameAgainstAi.move(f, t)) match {
                  case Success(playersMove) =>
                    playersMove match {
                      case Left(pm) =>
                        pm.printGameSummary
                        pm.counterWithAi match {
                          case Left(aim) =>
                            aim.printGameSummary
                            play(Option(aim))
                          case Right(result) => endGame(result)
                        }
                      case Right(result) => endGame(result)
                    }
                  case Failure(throwable) =>
                    println(s"Invalid move.$throwable")
                    play(tileGameAgainstAi)
                }
              case None =>
                println("Unknown move...")
                play(tileGameAgainstAi)
            }
        }
      case _ =>
        println("Unknown command...")
        play(tileGameAgainstAi)
    }
  }

  play()

}
