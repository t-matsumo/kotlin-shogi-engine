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

    private fun calcBestMove(startPosition: Position): Move {
        return startPosition.legalMoves().shuffled().maxByOrNull {  -negaMax(startPosition.moved(it), -calcMovePoint(it, startPosition)) } ?: MoveResign
    }

    private val INF = Long.MAX_VALUE / 2
    private fun negaMax(startPosition: Position, startScore: Long, depth: Int = 0): Long {
        val moves = startPosition.legalMoves().shuffled()

        if (moves.isEmpty()) {
            return -INF
        }

        if (depth == 2) {
            return startScore
        }

        var max = -INF
        for (move in moves) {
            val score = negaMax(
                startPosition.moved(move),
                -startScore - calcMovePoint(move, startPosition),
                depth + 1
            )
            max = max(max, -score)
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