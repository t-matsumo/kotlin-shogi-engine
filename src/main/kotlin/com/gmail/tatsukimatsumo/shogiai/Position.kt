package com.gmail.tatsukimatsumo.shogiai

sealed interface Position {
    val turnFor: Turn
    val moves: List<Move>
}
data class PositionWithSFEN(private val startPosition: SFEN, override val moves: List<Move>): Position {
    override val turnFor = startPosition.currentTurnFor()
}
data class PositionHirate(override val moves: List<Move>): Position {
    override val turnFor: Turn = if (moves.size % 2 == 0) Turn.FirstPlayer else Turn.SecondPlayer
}