package shogiai

sealed interface TimeLimit

data class TimeLimitFinite(
    val timeForFirstPlayer: Long,
    val timeForSecondPlayer: Long
): TimeLimit
data object TimeLimitInFinite: TimeLimit