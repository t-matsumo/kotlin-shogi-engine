package shogiai

import java.io.PrintWriter

class ShogiAIOutPutPortImpl(
    private val writer: PrintWriter
): ShogiAIOutPutPort {
    override fun execute(command: ShogiAIOutPutPort.Command) {
        writer.println(command.commandString)
        writer.flush()
    }
}