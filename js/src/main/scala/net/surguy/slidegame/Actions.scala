package net.surguy.slidegame

import net.surguy.slidegame.shared.{BoardState, Direction, Piece}
import org.scalajs.dom.raw.{HTMLDivElement, Node}

/**
  * Actions that change the state of the game.
  */
class Actions(var boardState: BoardState, rootDiv: HTMLDivElement) {

  new SvgBoardDisplay(boardState, rootDiv, this).display()

  def setActive(piece: Piece): Node = updateState(boardState.setActive(piece.name))

  def moveActive(direction: Direction): Any = {
    boardState.moveActivePiece(direction) match {
      case Some(newState) => updateState(newState)
      case None => // Invalid move - do nothing
    }
  }

  def restart(newState: BoardState): Node = updateState(newState)

  private def updateState(newState: BoardState) = {
    this.boardState = newState
    new SvgBoardDisplay(newState, rootDiv, this).display()
  }
}
