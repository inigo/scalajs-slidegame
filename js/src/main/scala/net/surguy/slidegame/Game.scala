package net.surguy.slidegame

import net.surguy.slidegame.shared._
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.html.Canvas

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalatags.JsDom.all._

/**
  * Entry point.
  */
@JSExportTopLevel("slidegame")
object Game {
  @JSExport
  def play(canvas: Canvas): Unit = {
    gameSelector()
  }

  val tileWidth = 30
  val tileHeight = 30

  private def gameSelector() = {
    val slide = dom.document.getElementById("slidegame")

    val startText = GameSetup.initialStates.map(s => span(onclick := { () => startGame(s) }, "Start again (%s)".format(s.name)).render )

    startText.map(slide.appendChild)
  }

  private[slidegame] def startGame(initialBoard: BoardState) = {
    println("Starting game")
    ???
  }


}
