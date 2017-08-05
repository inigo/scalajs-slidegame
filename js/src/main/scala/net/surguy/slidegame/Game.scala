package net.surguy.slidegame

import net.surguy.slidegame.shared._
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.HTMLDivElement

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

/**
  * Entry point.
  */
@JSExportTopLevel("slidegame")
object Game {
  @JSExport def play(canvas: Canvas): Unit = { startGame(GameSetup.easy) }

  private[slidegame] def startGame(initialBoard: BoardState) = {
    println("Starting game")
    val slide = dom.document.getElementById("slidegame").asInstanceOf[HTMLDivElement]
    new Actions(initialBoard, slide)
  }
}