package ShogiAI

class ShogiAIImpl(
    private val input: ShogiAIInputPort,
    private val output: ShogiAIOutPutPort
): ShogiAI {
    override fun execute() {
        var running = true
        while (running) {
            when (input.execute()) {
                is ShogiAIInputPort.USI -> {
                    output.execute(ShogiAIOutPutPort.IDName)
                    output.execute(ShogiAIOutPutPort.IDAuthoer)
                    output.execute(ShogiAIOutPutPort.USIOK)
                }
                is ShogiAIInputPort.SetOption -> { /* 何もしない */ }
                is ShogiAIInputPort.IsReady -> output.execute(ShogiAIOutPutPort.ReadyOK)
                is ShogiAIInputPort.USINewGame -> { running = false }
            }
        }
    }
}