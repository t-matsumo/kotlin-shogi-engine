package com.gmail.tatsukimatsumo.shogiai

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.concurrent.Volatile
import kotlin.math.max

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
        bestMove = MoveResign // とりあえず初期化
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
        val move = calcBestMove(position)
        bestMove = move
    }

    private val INF = Long.MAX_VALUE / 2
    private fun calcBestMove(startPosition: Position): Move {
        var bestMove: Move = MoveResign
        var alpha = -INF
        val sortedMoves = startPosition.legalMoves()
            .map { it to calcMovePoint(it, startPosition) }
            .sortedByDescending  { it.second }
        for ((move, moveScore) in sortedMoves) {
            val score = -negaAlpha(startPosition.moved(move), -moveScore, -INF, -alpha)
            if (score > alpha) {
                alpha = score
                bestMove = move
            }
        }

        return bestMove
    }

    private fun negaAlpha(startPosition: Position, startScore: Long, alpha: Long, beta: Long, depth: Int = 0): Long {
        val sortedMoves = startPosition.legalMoves()
            .map { it to calcMovePoint(it, startPosition) }
            .sortedByDescending { it.second }

        if (sortedMoves.isEmpty()) {
            return -INF + depth // 浅い探索で詰ませるほうが良い
        }

        if (depth == 3) {
            return startScore
        }

        var max = alpha
        for ((move, moveScore) in sortedMoves) {
            val score = -negaAlpha(
                startPosition.moved(move),
                -startScore - moveScore,
                -beta,
                -max,
                depth + 1
            )
            max = max(max, score)

            if (max >= beta) {
                return max
            }
        }

        return max
    }

    override suspend fun onMove() = withContext(defaultDispatcher) {
        if (bestMove !is MoveResign) {
            position = position.moved(bestMove)
        }
        bestMove
    }

    private fun calcMovePoint(move: Move, onPotision: Position): Long {
        val (row, coloumn) = move.toPosition()

        // とりあえず取った駒の価値を足す
        val pieceValue =  when (move) {
            is MoveDrop -> PieceValue(0)
            is MovePosition -> {
                val piece = onPotision.getOrNull(row, coloumn)
                piece?.pieceValue ?: PieceValue(0)
            }
            MoveResign -> error("MoveResign is not expected value.")
        }

        return pieceValue.value
    }
}