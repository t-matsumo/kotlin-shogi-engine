import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import shogiai.ShogiAI
import shogiai.ShogiAIImpl
import shogiai.ShogiAIInputPortImpl
import shogiai.ShogiAIOutPutPortImpl
import java.io.PrintWriter


fun main() {
    PrintWriter(System.out.bufferedWriter()).use { writer ->
        System.`in`.bufferedReader().use { reader ->
            val coroutineScope = CoroutineScope(Dispatchers.Default)
            try {
                val shogiAI: ShogiAI = ShogiAIImpl(
                    ShogiAIInputPortImpl(reader),
                    ShogiAIOutPutPortImpl(writer),
                    coroutineScope
                )
                shogiAI.execute()
            } finally {
                coroutineScope.cancel()
            }
        }
    }
}
