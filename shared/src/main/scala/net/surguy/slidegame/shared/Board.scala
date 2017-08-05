package net.surguy.slidegame.shared

case class Piece(name: String, shape: Shape, position: Position)
case class Shape(width: Int, height: Int, color: String)
case class Position(r: Int, c: Int)

sealed trait Direction
case object Up extends Direction
case object Down extends Direction
case object Left extends Direction
case object Right extends Direction

object Shapes {
  val big = Shape(width = 2, height = 2, color = "crimson")
  val small = Shape(width = 1, height = 1, color = "green")
  val wideRed = Shape(width = 2, height = 1, color = "crimson")
  val tall = Shape(width = 1, height = 2, color = "teal")
  val wide = Shape(width = 2, height = 1, color = "orange")
}

object GameSetup {
  import Shapes._

  val easy = BoardState(name = "Easy", numRows = 5, numCols = 4, activeName = "a", kingName = "a", winningPos = Position(4, 1), pieces = List(
    Piece("a", wideRed, Position(0, 1)),
    Piece("b", small, Position(0, 0)),
    Piece("c", small, Position(0, 3)),
    Piece("d", tall, Position(1, 0)),
    Piece("e", tall, Position(1, 1)),
    Piece("f", tall, Position(1, 2)),
    Piece("g", tall, Position(1, 3)),
    Piece("h", wide, Position(3, 1)),
    Piece("i", small, Position(3, 0)),
    Piece("j", small, Position(3, 3)),
    Piece("k", small, Position(4, 3))
  ))

  val classic = BoardState(name = "Classic Klotski", numRows = 5, numCols = 4, activeName = "a", kingName = "a", winningPos = Position(3, 1), pieces = List(
    Piece("a", big, Position(0, 1)),
    Piece("b", tall, Position(0, 0)),
    Piece("c", tall, Position(0, 3)),
    Piece("d", tall, Position(2, 0)),
    Piece("e", tall, Position(2, 3)),
    Piece("f", wide, Position(2, 1)),
    Piece("g", small, Position(4, 0)),
    Piece("h", small, Position(3, 1)),
    Piece("i", small, Position(3, 2)),
    Piece("j", small, Position(4, 3))
  ))

  val initialStates = List(easy, classic)

}

case class BoardState(name: String, numRows: Int, numCols: Int, activeName: String, kingName: String, winningPos: Position, pieces: List[Piece]) {
  assert(getPiece(kingName).isDefined, "King does not exist within pieces")
  assert(getPiece(activeName).isDefined, "No active piece")

  val king: Piece = getPiece(kingName).getOrElse(throw new IllegalStateException("King no longer exists"))
  val activePiece: Piece = getPiece(activeName).getOrElse(throw new IllegalStateException("No active piece"))
  def getPiece(name: String): Option[Piece] = pieces.find(_.name==name)

  val isGameOver: Boolean = king.position==winningPos

  def setActive(newActiveName: String): BoardState = this.copy(activeName = newActiveName)
  def moveActivePiece(direction: Direction): Option[BoardState] = {
    val pos = activePiece.position
    val newPosition = direction match {
      case Up    => pos.copy( r = pos.r - 1 )
      case Down  => pos.copy( r = pos.r + 1 )
      case Left  => pos.copy( c = pos.c - 1 )
      case Right => pos.copy( c = pos.c + 1 )
    }
    if (isGameOver) None else Some(moveActivePiece(newPosition)).filter(_.isValid)
  }

  private[shared] def moveActivePiece(newPosition: Position): BoardState = updatePiece(activePiece.copy(position = newPosition))
  private def updatePiece(movedPiece: Piece): BoardState = {
    val newPieces = movedPiece :: pieces.filterNot(_.name == movedPiece.name)
    this.copy(pieces = newPieces)
  }

  private[shared] def isValid: Boolean = noneOutsideBoard && noOverlaps
  private[shared] val noneOutsideBoard: Boolean = pieces.flatMap(coverage).forall(p => p.c>=0 && p.c<numRows && p.r>=0 && p.r<numCols)
  private[shared] val noOverlaps: Boolean = {
    val allCoveredPositions = pieces.flatMap(coverage)
    allCoveredPositions.distinct.length == allCoveredPositions.length
  }

  private[shared] def coverage(piece: Piece): Seq[Position] = {
    for (c <- piece.position.c until piece.position.c + piece.shape.width;
         r <- piece.position.r until piece.position.r + piece.shape.height) yield Position(c, r)
  }

}