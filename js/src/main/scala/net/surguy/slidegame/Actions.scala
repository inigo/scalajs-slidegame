package net.surguy.slidegame

import net.surguy.slidegame.shared.{BoardState, Direction, GameSetup, Piece}

class Observable() {
  private var observers: List[Observer[_]] = List()
  def registerObserver[T <: Message](observer: Observer[T]): Unit = { observers = observer :: observers }
  def notifyObservers[T <: Message](message: T): Unit = {
    observers.filter(_.isAppropriateFor(message)).foreach(o => o.asInstanceOf[Observer[T]].notify(message))
  }
}

abstract class Observer[T <: Message] {
  def isAppropriateFor(msg: Message): Boolean
  def notify(msg: T)
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

  class SetActiveObserver extends Observer[SetActive] {
    override def isAppropriateFor(msg: Message): Boolean = msg.isInstanceOf[SetActive]
    override def notify(msg: SetActive): Unit = {
      val newState = boardState.setActive(msg.piece.name)
      observable.notifyObservers(UpdateBoard(newState))
    }
  }

  class MoveActiveObserver extends Observer[MoveActive] {
    override def isAppropriateFor(msg: Message): Boolean = msg.isInstanceOf[MoveActive]
    override def notify(msg: MoveActive): Unit = {
      boardState.moveActivePiece(msg.direction) match {
        case Some(newState) => observable.notifyObservers(UpdateBoard(newState))
        case None => // Invalid mode - do nothing
      }
    }
  }

  class ResetObserver extends Observer[Reset] {
    override def isAppropriateFor(msg: Message): Boolean = msg.isInstanceOf[Reset]
    override def notify(msg: Reset): Unit = {
      GameSetup.initialStates.find(_.name==msg.stateName) match {
        case Some(resetState) => observable.notifyObservers(UpdateBoard(resetState))
        case None => throw new IllegalStateException("Attempting to reset state to unknown state "+msg.stateName)
      }
    }
  }

  class UpdateBoardObserver extends Observer[UpdateBoard] {
    override def isAppropriateFor(msg: Message): Boolean = msg.isInstanceOf[UpdateBoard]
    override def notify(msg: UpdateBoard): Unit = {
      boardState = msg.newBoard
      displayFn(boardState, observable)
    }
  }

  observable.registerObserver(new SetActiveObserver())
  observable.registerObserver(new MoveActiveObserver())
  observable.registerObserver(new ResetObserver())
  observable.registerObserver(new UpdateBoardObserver())

  observable.notifyObservers(UpdateBoard(boardState))
}
