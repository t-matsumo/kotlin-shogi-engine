import ShogiAI.ShogiAI
import ShogiAI.ShogiAIImpl
import ShogiAI.ShogiAIInputPortImpl
import ShogiAI.ShogiAIOutPutPortImpl
import java.io.PrintWriter

fun main() {
    PrintWriter(System.out.bufferedWriter()).use { writer ->
        System.`in`.bufferedReader().use { reader ->
            val shogiAI: ShogiAI = ShogiAIImpl(
                ShogiAIInputPortImpl(reader),
                ShogiAIOutPutPortImpl(writer),
            )
            shogiAI.execute()
        }
    }
}
