package net.surguy.slidegame

import net.surguy.slidegame.shared.{BoardState, Direction, GameSetup, Piece}

class Observable() {
  type Observer = PartialFunction[Message, Unit]

  private var observers: List[Observer] = List()
  def registerObserver[T <: Message](observer: Observer): Unit = { observers = observer :: observers }
  def notifyObservers[T <: Message](message: T): Unit = {
    observers.filter(_.isDefinedAt(message)).foreach(o => o(message))
  }
}

sealed trait Message
case class Reset(stateName: String) extends Message
case class MoveActive(direction: Direction) extends Message
case class SetActive(piece: Piece) extends Message
case class UpdateBoard(newBoard: BoardState) extends Message
case class TimeTravel(isBackward: Boolean)

/**
  * Actions that change the state of the game.
  */
class GameActions(private var boardState: BoardState, observable: Observable, displayFn: (BoardState, Observable) => Unit) {
  val observers: Seq[PartialFunction[Message, Unit]] = List(
    { case SetActive(piece) =>
      val newState = boardState.setActive(piece.name)
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
