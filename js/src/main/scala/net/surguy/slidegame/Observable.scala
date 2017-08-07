package net.surguy.slidegame

import net.surguy.slidegame.shared.Message

class Observable() {
  type Observer = PartialFunction[Message, Unit]

  private var observers: List[Observer] = List()
  def registerObserver(observer: Observer): Unit = { observers = observer :: observers }
  def unregisterObserver(observer: Observer): Unit = { observers = observers.diff(List(observer)) }
  def notifyObservers[T <: Message](message: T): Unit = {
    observers.filter(_.isDefinedAt(message)).foreach(o => o(message))
  }
}
