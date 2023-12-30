package com.gmail.tatsukimatsumo.shogiai

sealed interface Move {
    companion object {
        fun valueOf(value: String): Move = when {
            value.contains("*") -> MoveDrop(value)
            else -> MovePosition(value)
        }
    }
    val value: String
}

data class MovePosition(override val value: String): Move {
    private companion object {
        val valueRegex = Regex("[1-9][a-i][1-9][a-i]\\+?")
    }
    init {
        require(valueRegex.matches(value))
    }

    fun fromPosition(): Pair<Row, Column> = Row.valueOf(value[1]) to Column.valueOf(value[0])
    fun fromPositonLesserInt(): Int {
        val (row, column) = fromPosition()
        return row.lesserIntValue + column.lesserIntValue
    }

    fun toPosition(): Pair<Row, Column> = Row.valueOf(value[3]) to Column.valueOf(value[2])
    fun toPositonLesserInt(): Int {
        val (row, column) = toPosition()
        return row.lesserIntValue + column.lesserIntValue
    }

    fun promoted(): Boolean = value.contains("+")
}
data class MoveDrop(override val value: String): Move {
    private companion object {
        val valueRegex = Regex("[LNSGBRP]\\*[1-9][a-i]")
    }
    init {
        require(valueRegex.matches(value))
    }

    fun dropPosition(): Pair<Row, Column> = Row.valueOf(value[3]) to Column.valueOf(value[2])
    fun dropPositonLesserInt(): Int {
        val (row, column) = dropPosition()
        return row.lesserIntValue + column.lesserIntValue
    }

    fun dropKoma(): Piece = Piece.valueOf(value.first())
}
data object MoveResign: Move {
    override val value: String = "resign"
}

enum class Row(private val intValue: Int, val charValue: Char) {
    A(1, 'a'),
    B(2, 'b'),
    C(3, 'c'),
    D(4, 'd'),
    E(5, 'e'),
    F(6, 'f'),
    G(7, 'g'),
    H(8, 'h'),
    I(9, 'i');

    val lesserIntValue = intValue

    companion object {
        fun valueOf(charValue: Char): Row = Row.entries.first { it.charValue == charValue }
        fun valueOf(intValue: Int): Row = Row.entries.first { it.intValue == intValue }
    }
}

enum class Column(private val intValue: Int) {
    One(1),
    Two(2),
    Three(3),
    Four(4),
    Five(5),
    Six(6),
    Seven(7),
    Eight(8),
    Nine(9);

    val lesserIntValue = intValue shl 4
    val charValue: Char = intValue.digitToChar()

    companion object {
        fun valueOf(charValue: Char): Column = Column.entries.first { it.charValue == charValue }
        fun valueOf(intValue: Int): Column = Column.entries.first { it.intValue == intValue }
    }
}
