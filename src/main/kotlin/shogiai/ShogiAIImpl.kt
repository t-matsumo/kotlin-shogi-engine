package shogiai

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.concurrent.Volatile

class ShogiAIImpl(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): ShogiAI {
    @Volatile
    private var bestMove = MoveResign

    override suspend fun getProgramName(): ProgramName = withContext(defaultDispatcher) {
        ProgramName("ShogiAI")
    }

    override suspend fun getAuthorName(): AuthorName = withContext(defaultDispatcher) {
        AuthorName("t-matsumo")
    }

    override suspend fun onPrepareForGame() = withContext(defaultDispatcher) {
        bestMove = MoveResign
    }

    override suspend fun onReceivePosition(position: Position) = withContext(defaultDispatcher) {
        when (position) {
            is PositionHirate -> bestMove = MoveResign // これを実装する
            is PositionWithSFEN -> bestMove = MoveResign // 対応していないので投了
        }
    }

    override suspend fun onStartToPonder(timeLimit: TimeLimit) = withContext(defaultDispatcher) {
        // いったん何もしない
    }

    override suspend fun onMove() = withContext(defaultDispatcher) {
        bestMove
    }
}
