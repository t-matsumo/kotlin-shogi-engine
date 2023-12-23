package ShogiAI

interface ShogiAIInputPort {
    companion object {
        fun commandOf(commandString: String): Command {
            return when {
                commandString == "usi" -> USI
                commandString.startsWith("setoption") -> SetOption(commandString)
                commandString == "isready" -> IsReady
                commandString == "usinewgame" -> USINewGame
                else -> error("the command is not defined")
            }
        }
    }

    sealed interface Command

    data object USI: Command
    data class SetOption(private val commandString: String): Command {
        private val name: String
        private val value: String?

        init {
            val input = commandString.split(" ")
            name = input[2]
            value = input.getOrNull(4)
        }
    }
    data object IsReady: Command
    data object USINewGame: Command

    fun execute(): Command
}