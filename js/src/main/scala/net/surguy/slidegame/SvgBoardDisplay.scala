package net.surguy.slidegame

import net.surguy.slidegame.shared.{BoardState, Down, GameSetup, Left, Piece, Right, Up}
import org.scalajs.dom.KeyboardEvent
import org.scalajs.dom.raw.{HTMLDivElement, Node}

import scalatags.JsDom.svgAttrs
import scalatags.JsDom.all._

/**
  * Render the current board in SVG.
  */
class SvgBoardDisplay(boardState: BoardState, rootDiv: HTMLDivElement, actions: Observable) {
  import scalatags.JsDom.svgAttrs._
  import scalatags.JsDom.svgTags._

  private val boardWidth = boardState.numCols * 100
  private val boardHeight = boardState.numRows * 100

  def display(): Node = {
    val viewBoxSize = "0 0 "+boardWidth+" "+(boardHeight+20)
    val svgBoard = div (padding := "20px", svg(
      List(svgAttrs.attr("version"):="1.1", width := "550px", height := "550px", x := "0", y := "0", viewBox := viewBoxSize, renderBoard())
        ::: boardState.pieces.map(renderPiece)
        :_* ), if(boardState.isGameOver) renderGameOver() else renderInstructions(), renderResetLink() )

    while (rootDiv.hasChildNodes()) { rootDiv.removeChild(rootDiv.firstChild) }
    rootDiv.ownerDocument.onkeyup = { (evt: KeyboardEvent) => handleKeyPress(if (evt.keyCode!=0) evt.keyCode else evt.charCode) }
    rootDiv.appendChild(svgBoard.render )
  }

  private def handleKeyPress(key: Int): Unit = {
    val keyName = key.toChar.toLower.toString
    val selectedPiece = boardState.pieces.find(_.name == keyName)
    (key, selectedPiece) match {
      case (_, Some(piece))  => actions.notifyObservers(SetActive(piece))
      case (37,_) => actions.notifyObservers(MoveActive(Left))
      case (38,_) => actions.notifyObservers(MoveActive(Up))
      case (39,_) => actions.notifyObservers(MoveActive(Right))
      case (40,_) => actions.notifyObservers(MoveActive(Down))
      case _ => // Do nothing
    }
  }

  private def renderBoard() = rect(width := boardWidth, height := boardHeight, fill := "black")

  private def renderInstructions() = p ( "Playing " + boardState.name + ". Get the red block to the exit at the bottom. " +
    " Choose block by letter or with mouse, move with arrow keys.")

  private def renderResetLink() = p ( GameSetup.initialStates.map(s => span( onclick := { () => actions.notifyObservers(Reset(s.name)) }, "Start again (%s) | ".format(s.name))) :_* )

  private def renderGameOver() = h1 ( style:= "color: red", "You completed it!" )

  private def renderPiece(piece: Piece) = {
    val isActive = piece.name == boardState.activeName
    val flash = animate (attributeType:= "XML",  attributeName:= "fill-opacity", from:= "1.0", to:= "0.5", dur:= "1s", repeatCount:= "indefinite")

    val flashNode = if (isActive && !boardState.isGameOver) flash else text()
    val xPos = 100 * piece.position.c
    val yPos = 100 * piece.position.r
    val w = 100 * piece.shape.width
    val h = 100 * piece.shape.height
    val xMid = xPos + (w/2)
    val yMid = yPos + (h/2)
    val opacity = if (boardState.isGameOver) 0.5 else 1.0

    g(onclick := { () => actions.notifyObservers(SetActive(piece)) },
      rect(x := xPos, y := yPos, width := w, height := h, stroke := "black", strokeWidth := 5, fill := piece.shape.color, fillOpacity := opacity, flashNode),
      text(x := xMid, y:= yMid+10, fontSize := 50, textAnchor := "middle", fill := (if (isActive) "black" else "white"),  piece.name)
    )
  }

}
