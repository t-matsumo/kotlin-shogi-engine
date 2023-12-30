package com.gmail.tatsukimatsumo.shogiai

enum class Piece(val charValue: Char, val lesserIntValue: Int, val pieceValue: PieceValue, val promotedPieceValue: PieceValue) {
    Lance('L', 2, PieceValue(3), PieceValue(6)),
    Night('N', 3, PieceValue(4), PieceValue(6)),
    Silver('S', 4, PieceValue(5), PieceValue(6)),
    Gold('G', 5, PieceValue(6), PieceValue(6)),
    King('K', 8, PieceValue(2000), PieceValue(2000)),
    Bishop('B', 6, PieceValue(9), PieceValue(13)),
    Rook('R', 7, PieceValue(10), PieceValue(15)),
    Pawn('P', 1, PieceValue(1), PieceValue(8));

    companion object {
        fun valueOf(charValue: Char) = Piece.entries.first { it.charValue == charValue }
        fun valueOf(lesserIntValue: Int) = Piece.entries.first { it.lesserIntValue == lesserIntValue }
    }

    fun pieceValue(promoted: Boolean): PieceValue = if (promoted) promotedPieceValue else pieceValue
}


@JvmInline
value class PieceValue(val value: Int) {
    operator fun compareTo(other: PieceValue): Int = this.value.compareTo(other.value)
}