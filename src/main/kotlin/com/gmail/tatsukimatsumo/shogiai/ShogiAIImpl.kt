package com.gmail.tatsukimatsumo.shogiai

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.concurrent.Volatile

class ShogiAIImpl(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): ShogiAI {
    @Volatile
    private var bestMove: Move = MoveResign
    @Volatile
    private var position: Position = PositionByLesserpyon()

    override suspend fun getProgramName(): ProgramName = withContext(defaultDispatcher) {
        ProgramName("ShogiAI")
    }

    override suspend fun getAuthorName(): AuthorName = withContext(defaultDispatcher) {
        AuthorName("t-matsumo")
    }

    override suspend fun onPrepareForGame() = withContext(defaultDispatcher) {
        position = PositionByLesserpyon()
        bestMove = MoveResign
    }

    override suspend fun onReceivePosition(positionUSI: PositionUSI) = withContext(defaultDispatcher) {
        when (positionUSI) {
            is PositionUSIHirate -> {
                val lastMove = positionUSI.lastMoveOrNull()
                if (lastMove != null) { // movesが無い時は先手番の初手なので何もしない
                    // 相手の手を反映させる
                    position = position.moved(lastMove)
                }
            }
            is PositionUSIWithSFEN -> bestMove = MoveResign // 対応していないので投了
        }
    }

    override suspend fun onStartToPonder(timeLimit: TimeLimit) = withContext(defaultDispatcher) {
        val legalMoves = position.legalMoves().shuffled()

        // 探索が打ち切られた時のために、一つ選んでおく
        bestMove = legalMoves.getOrElse(0) { MoveResign }

        var maxPoint = PieceValue(Long.MIN_VALUE)
        for (move in legalMoves) {
            val nextPosition = position.moved(move)
            if (nextPosition.isGameOver()) {
                // 相手を詰ませるなら、その手を選ぶ
                bestMove = move
                break
            }

            val point = if (position.turnFor == Turn.FirstPlayer) nextPosition.point else -nextPosition.point
            if (point >= maxPoint) {
                bestMove = move
                maxPoint = point
            }
        }
    }

    override suspend fun onMove() = withContext(defaultDispatcher) {
        if (bestMove !is MoveResign) {
            position = position.moved(bestMove)
        }
        bestMove
    }
}
