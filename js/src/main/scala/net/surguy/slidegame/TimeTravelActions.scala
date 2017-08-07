package net.surguy.slidegame

import scala.collection.mutable.ListBuffer

/**
  * Support stepping forwards and backwards through the history.
  */
class TimeTravelActions(observable: Observable) {
  private val pastActions = new ListBuffer[Message]()
  private val futureActions = new ListBuffer[Message]()

  private val recordingObservers: Seq[PartialFunction[Message, Unit]] = List(
    { case msg: MoveActive => pastActions.append(msg); futureActions.clear() },
    { case msg: SetActive => pastActions.append(msg); futureActions.clear() },
    { case Reset(_) => pastActions.clear(); futureActions.clear() },
  )

  private val ttObserver: Seq[PartialFunction[Message, Unit]] = List(
    { case TimeTravel(true) if pastActions.isEmpty => // Do nothing
      case TimeTravel(true) =>
        println("Stepping backward")
        val actionToReplay = pastActions.remove(pastActions.size-1)
        notifyWithoutRecording(invert(actionToReplay))
        futureActions.insert(0, actionToReplay)
      case TimeTravel(false) if futureActions.isEmpty => // Do nothing
      case TimeTravel(false) =>
        println("Stepping forward")
        val actionToReplay = futureActions.remove(0)
        notifyWithoutRecording(actionToReplay)
        pastActions.append(actionToReplay)
    })

  private def notifyWithoutRecording(msg: Message) = {
    recordingObservers.foreach(observable.unregisterObserver)
    observable.notifyObservers(msg)
    recordingObservers.foreach(observable.registerObserver)
  }

  private def invert(msg: Message): Message = {
    import net.surguy.slidegame.shared._
    msg match {
      case MoveActive(Left) => MoveActive(Right)
      case MoveActive(Right) => MoveActive(Left)
      case MoveActive(Up) => MoveActive(Down)
      case MoveActive(Down) => MoveActive(Up)
      case SetActive(previousPiece, newPiece) => SetActive(newPiece, previousPiece)
      case _ => msg
    }
  }

  ttObserver.foreach(observable.registerObserver)
  recordingObservers.foreach(observable.registerObserver)
}
