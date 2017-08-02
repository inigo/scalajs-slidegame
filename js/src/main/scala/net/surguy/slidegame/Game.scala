package net.surguy.slidegame

import net.surguy.slidegame.shared._
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{HTMLDivElement, Node}

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalatags.JsDom.all._
import scalatags.JsDom.svgAttrs

/**
  * Entry point.
  */
@JSExportTopLevel("slidegame")
object Game {
  @JSExport def play(canvas: Canvas): Unit = { startGame(GameSetup.easy) }

  private[slidegame] def startGame(initialBoard: BoardState) = {
    println("Starting game")
    val slide = dom.document.getElementById("slidegame").asInstanceOf[HTMLDivElement]
    new Board(initialBoard, slide).display()
  }
}

class Board(var boardState: BoardState, rootDiv: HTMLDivElement) {

  import scalatags.JsDom.svgAttrs._
  import scalatags.JsDom.svgTags._

  private val boardWidth = boardState.numCols * 100
  private val boardHeight = boardState.numRows * 100

  def display(): Node = {
    val viewBoxSize = "0 0 "+boardWidth+" "+(boardHeight+20)
    val svgBoard = div (padding := "20px", svg(
      List(svgAttrs.attr("version"):="1.1", width := "550px", height := "550px", x := "0", y := "0", viewBox := viewBoxSize, renderBoard())
        ::: boardState.pieces.map(renderPiece)
        :_* ), renderInstructions(), renderResetLink() )

    while (rootDiv.hasChildNodes()) { rootDiv.removeChild(rootDiv.firstChild) }
    rootDiv.appendChild(svgBoard.render )
  }

  def setActive(piece: Piece): Node = {
    this.boardState = boardState.setActive(piece.name)
    display()
  }
  def reset(newState: BoardState): Node = {
    this.boardState = newState
    display()
  }

  private def renderBoard() = rect(width := boardWidth, height := boardHeight, fill := "black")

  private def renderInstructions() = p ( "Playing " + boardState.name + ". Get the red block to the exit at the bottom. " +
    " Choose block by letter or with mouse, move with arrow keys.")

  private def renderResetLink() = p ( GameSetup.initialStates.map(s => span( onclick := { () => reset(s) }, "Start again (%s)".format(s.name))) :_* )

  private def renderPiece(piece: Piece) = {
    val isActive = piece.name == boardState.activeName
    val flash = animate (attributeType:= "XML",  attributeName:= "fill-opacity", from:= "1.0", to:= "0.5", dur:= "3s", repeatCount:= "indefinite")

    val flashNode = if (isActive && !boardState.isGameOver) flash else text()
    val xPos = 100 * piece.position.c
    val yPos = 100 * piece.position.r
    val w = 100 * piece.shape.width
    val h = 100 * piece.shape.height
    val xMid = xPos + (w/2)
    val yMid = yPos + (h/2)
    val opacity = if (boardState.isGameOver) 0.5 else 1.0

    g(onclick := { () => setActive(piece) },
      rect(x := xPos, y := yPos, width := w, height := h, stroke := "black", strokeWidth := 5, fill := piece.shape.color, fillOpacity := opacity, flashNode),
      text(x := xMid, y:= yMid+10, fontSize := 50, textAnchor := "middle", fill := "white",  piece.name)
    )
  }

}