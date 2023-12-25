package shogiai

import jp.usapyonsoft.lesserpyon.GenerateMoves
import jp.usapyonsoft.lesserpyon.Kyokumen
import kotlinx.coroutines.*

class ShogiAIImpl(
    private val input: ShogiAIInputPort,
    private val output: ShogiAIOutPutPort,
    private val coroutineScope: CoroutineScope
): ShogiAI {
    override fun execute() {
        var currentPosition: ShogiAIInputPort.Position? = null
        while (true) {
            when (val command = input.execute()) {
                is ShogiAIInputPort.USI -> {
                    output.execute(ShogiAIOutPutPort.IDName)
                    output.execute(ShogiAIOutPutPort.IDAuthoer)
                    output.execute(ShogiAIOutPutPort.USIOK)
                }
                is ShogiAIInputPort.SetOption -> { /* 何もしない */ }
                is ShogiAIInputPort.IsReady -> output.execute(ShogiAIOutPutPort.ReadyOK)
                is ShogiAIInputPort.USINewGame -> { /* 何もしない */ }
                is ShogiAIInputPort.Position -> { currentPosition = command }
//                is ShogiAIInputPort.Go -> { // ここで考えて、手を返す
//                    coroutineScope.launch(Dispatchers.Default) {
//                        try {
//                            val bestMoveString= ponderFrom(currentPosition!!)
//                            withContext(Dispatchers.IO) {
//                                output.execute(ShogiAIOutPutPort.BestMove(bestMoveString))
//                            }
//                        } catch (e: Throwable) {
//                            // 例外が発生したら負けにする
//                            withContext(Dispatchers.IO) {
//                                output.execute(ShogiAIOutPutPort.BestMove("bestmove resign"))
//                            }
//                        }
//                    }
//                }
                is ShogiAIInputPort.Gameover -> break
                else -> error("")
            }
        }
    }

    private fun ponderFrom(position: ShogiAIInputPort.Position): String {
        // 現在の盤面から合法手をすべて洗い出し、
        // 評価関数を用いて最も良い手を出力する
        return "3c3d"
    }
}
