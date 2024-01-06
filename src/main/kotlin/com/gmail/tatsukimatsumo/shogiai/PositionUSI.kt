package com.gmail.tatsukimatsumo.shogiai

sealed interface PositionUSI {
    val turnFor: Turn
    val moves: List<Move>

    fun lastMoveOrNull(): Move? = moves.lastOrNull()
}
data class PositionUSIWithSFEN(private val startPosition: SFEN, override val moves: List<Move>): PositionUSI {
    override val turnFor = startPosition.currentTurnFor()
}
data class PositionUSIHirate(override val moves: List<Move>): PositionUSI {
    override val turnFor: Turn = if (moves.size % 2 == 0) Turn.FirstPlayer else Turn.SecondPlayer
}