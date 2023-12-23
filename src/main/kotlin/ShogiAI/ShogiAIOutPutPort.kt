package ShogiAI

interface ShogiAIOutPutPort {
    interface Command {
        val commandString: String
    }
    data object IDName: Command {
        override val commandString = "id name ShogiAI"
    }
    data object IDAuthoer: Command {
        override val commandString = "id authoer t-matsumo"
    }
    data object USIOK: Command {
        override val commandString = "usiok"
    }
    data object ReadyOK: Command {
        override val commandString = "readyok"
    }

    fun execute(command: Command)
}
