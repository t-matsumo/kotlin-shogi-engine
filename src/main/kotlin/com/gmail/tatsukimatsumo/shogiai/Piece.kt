package com.gmail.tatsukimatsumo.shogiai

enum class Piece(val charValue: Char, val lesserIntValue: Int, val pieceValue: PieceValue) {
    Lance('L', 2, PieceValue(3)),
    Knight('N', 3, PieceValue(4)),
    Silver('S', 4, PieceValue(5)),
    Gold('G', 5, PieceValue(6)),
    King('K', 8, PieceValue(2000)),
    Bishop('B', 6, PieceValue(9)),
    Rook('R', 7, PieceValue(10)),
    Pawn('P', 1, PieceValue(1)),
    PromotedPawn('P', 100, PieceValue(8)),
    PromotedLance('P', 100, PieceValue(6)),
    PromotedKnight('P', 100, PieceValue(6)),
    PromotedSilver('P', 100, PieceValue(6)),
    PromotedBishop('P', 100, PieceValue(13)),
    PromotedRook('P', 100, PieceValue(15));

    companion object {
        fun valueOf(charValue: Char) = Piece.entries.first { it.charValue == charValue }
        fun valueOf(lesserIntValue: Int) = Piece.entries.first { it.lesserIntValue == lesserIntValue }
    }
}


@JvmInline
value class PieceValue(val value: Long) {
    operator fun compareTo(other: PieceValue): Int = this.value.compareTo(other.value)

    operator fun plus(other: PieceValue): PieceValue = PieceValue(this.value + other.value)
    operator fun minus(other: PieceValue): PieceValue = PieceValue(this.value - other.value)
    operator fun unaryMinus(): PieceValue = PieceValue(-this.value)
}