package net.surguy.slidegame.shared

import org.specs2.mutable.Specification


object BoardStateTest extends Specification {
  private val boardState = GameSetup.easy

  "Game state" should {
    "start off valid" in { boardState.isValid must beTrue }
    "be invalid if a piece is overlapping" in { boardState.moveActivePiece(Position(2, 2)).noOverlaps must beFalse }
    "be invalid if a piece is off the board to the left" in { boardState.moveActivePiece(Position(-1, 2)).noneOutsideBoard must beFalse }
    "be invalid if a piece is off the board to the right" in { boardState.setActive("k").moveActivePiece(Position(4, 4)).noneOutsideBoard must beFalse }
    "still be valid if moving piece 'i' downwards" in { boardState.setActive("i").moveActivePiece( Down ) must beSome }
  }
  "Calculating board coverage" should {
    "cover 17 squares for an easy board" in {  GameSetup.easy.pieces.flatMap(boardState.coverage) must haveSize(17)  }
    "cover 18 squares for a standard board" in {  GameSetup.classic.pieces.flatMap(boardState.coverage) must haveSize(18)  }
  }

}
