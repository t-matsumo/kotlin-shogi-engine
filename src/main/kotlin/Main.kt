import infrastructure.InputReaderImpl
import infrastructure.OutPutWriterImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import shogiai.ShogiAIImpl
import shogiengine.usi.ShogiEngineForUSIProtocol
import java.io.PrintWriter


fun main() {
    PrintWriter(System.out.bufferedWriter()).use { writer ->
        System.`in`.bufferedReader().use { reader ->
            val coroutineScope = CoroutineScope(Dispatchers.Default)
            try {
                ShogiEngineForUSIProtocol(
                    InputReaderImpl(reader),
                    OutPutWriterImpl(writer),
                    coroutineScope,
                    ShogiAIImpl()
                ).execute()
            } finally {
                coroutineScope.cancel()
            }
        }
    }
}
