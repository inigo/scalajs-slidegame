package net.surguy.slidegame

import net.surguy.slidegame.shared.{BoardState, Direction, Piece}

/**
  * Actions that change the state of the game.
  */
class Actions(private var boardState: BoardState, displayFn: (BoardState, Actions) => Unit) {

  displayFn(boardState, this)

  def setActive(piece: Piece): Unit = updateState(boardState.setActive(piece.name))

  def moveActive(direction: Direction): Unit = {
    boardState.moveActivePiece(direction) match {
      case Some(newState) => updateState(newState)
      case None => // Invalid move - do nothing
    }
  }

  def restart(newState: BoardState): Unit = updateState(newState)

  private def updateState(newState: BoardState) = {
    this.boardState = newState
    displayFn(boardState, this)
  }
}
