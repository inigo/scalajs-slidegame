package net.surguy.slidegame.shared

/**
  * Actions that change the state of the game.
  */
class GameActions(private var boardState: BoardState, observable: Observable, displayFn: (BoardState, Observable) => Unit) {
  val observers: Seq[PartialFunction[Message, Unit]] = List(
    { case SetActive(previousPiece, newPiece) =>
      val newState = boardState.setActive(newPiece.name)
      observable.notifyObservers(UpdateBoard(newState))
    },
    { case MoveActive(direction) =>
      boardState.moveActivePiece(direction) match {
        case Some(newState) => observable.notifyObservers(UpdateBoard(newState))
        case None => // Invalid mode - do nothing
      }
    },
    { case Reset(stateName) =>
       GameSetup.initialStates.find(_.name==stateName) match {
         case Some(resetState) => observable.notifyObservers(UpdateBoard(resetState))
         case None => throw new IllegalStateException("Attempting to reset state to unknown state "+stateName)
       }
    },
    { case UpdateBoard(newBoard) =>
      boardState = newBoard
      displayFn(boardState, observable)
    },
  )

  observers.foreach(observable.registerObserver)
  observable.notifyObservers(UpdateBoard(boardState))
}
