package shogiai

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
}
data class MoveDrop(override val value: String): Move {
    private companion object {
        val valueRegex = Regex("[LNSGBRP]\\*[1-9][a-i]")
    }
    init {
        require(valueRegex.matches(value))
    }
}
data object MoveResign: Move {
    override val value: String = "resign"
}