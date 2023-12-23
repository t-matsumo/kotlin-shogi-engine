package ShogiAI

import java.io.BufferedReader

class ShogiAIInputPortImpl(private val reader: BufferedReader): ShogiAIInputPort {
    override fun execute() = ShogiAIInputPort.commandOf(reader.readLine())
}

