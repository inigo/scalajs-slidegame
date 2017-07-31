package net.surguy.slidegame.shared

import org.specs2.mutable.Specification


object BoardStateTest extends Specification {
  private val boardState = GameSetup.easy

  "Game state" should {
    "start off valid" in { boardState.isValid must beTrue }
    "be invalid if a piece is overlapping" in { boardState.moveActivePiece(Position(2, 2)).noOverlaps must beFalse }
    "be invalid if a piece is off the board" in { boardState.moveActivePiece(Position(-1, 2)).noneOutsideBoard must beFalse }
    "still be valid if moving piece 'i' downwards" in {
      val initialPosition = boardState.getPiece("i").get.position
      boardState.setActive("i").moveActivePiece( initialPosition.copy(r = initialPosition.r+1) ).isValid must beTrue
    }
  }

}
