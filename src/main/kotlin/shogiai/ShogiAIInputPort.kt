package shogiai

interface ShogiAIInputPort {
    companion object {
        fun commandOf(commandString: String): Command {
            return when {
                commandString == "usi" -> USI
                commandString.startsWith("setoption") -> SetOption(commandString)
                commandString == "isready" -> IsReady
                commandString == "usinewgame" -> USINewGame
                commandString.startsWith("position") -> Position(commandString)
                commandString.startsWith("go") -> {
                    val input = commandString.split(" ")
                    when {
                        input[1] == "ponder" -> error("ponder is not defined")
                        input[1] == "infinite" -> GoInfiniteTime
                        input[1] == "mate" -> {
                            if (input[2] == "infinite") GoMateInfiniteTime else GoMateFiniteTime(commandString)
                        }
                        input[5] == "byoyomi" -> GoByoyomi(commandString)
                        input[5] == "binc " -> GoFischerClock(commandString)
                        else -> error("the command is not defined for $commandString")
                    }
                }
                commandString.startsWith("gameover") -> {
                    val result = commandString.split(" ")[1]
                    Gameover.entries.first { it.commandString == result }
                }
                commandString == "stop" -> Stop
                commandString == "ponderhit" -> Ponderhit
                commandString == "quit" -> Quit
                else -> error("the command is not defined for $commandString")
            }
        }
    }

    sealed interface Command

    data object USI: Command
    data class SetOption(private val commandString: String): Command
    data object IsReady: Command
    data object USINewGame: Command
    data class Position(private val commandString: String): Command
    interface FiniteTime {
        val btime: Long
        val wtime: Long
    }
    data class GoByoyomi(private val commandString: String): FiniteTime, Command {
        override val btime: Long
        override val wtime: Long
        init {
            // どちらの手番でも秒読みの時間を足すので、とりあえず加算するだけ
            val input = commandString.split(" ")
            btime = input[2].toLong() + input[6].toLong()
            wtime = input[4].toLong() + input[6].toLong()
        }
    }
    data class GoFischerClock(private val commandString: String): FiniteTime, Command {
        override val btime: Long
        override val wtime: Long
        init {
            val input = commandString.split(" ")
            btime = input[2].toLong() + input[6].toLong()
            wtime = input[4].toLong() + input[8].toLong()
        }
    }
    data object GoInfiniteTime: Command
    data class GoMateFiniteTime(private val commandString: String): Command {
        val time: Long = commandString.split(" ")[2].toLong()
    }
    data object GoMateInfiniteTime: Command
    enum class Gameover(val commandString: String): Command {
        WIN("win"),
        LOSE("lose"),
        DRAW("draw")
    }
    data object Stop: Command
    data object Ponderhit: Command
    data object Quit: Command

    fun execute(): Command
}