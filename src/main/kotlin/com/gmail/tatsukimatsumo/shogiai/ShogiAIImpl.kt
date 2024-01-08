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

        // 詰ませる手があるなら、それを選ぶ
        val checkMateMove = legalMoves.firstOrNull { position.moved(it).isGameOver() }
        if (checkMateMove != null) {
            bestMove = checkMateMove
        } else {
            // 探索が打ち切られた時のために、一つ選んでおく
            bestMove = legalMoves.firstOrNull() ?: MoveResign

            var maxPoint = PieceValue(Long.MIN_VALUE)
            for (move in legalMoves) {
                val point = calcMovePoint(move)
                if (point > maxPoint) {
                    bestMove = move
                    maxPoint = point
                }
            }
        }
    }

    override suspend fun onMove() = withContext(defaultDispatcher) {
        if (bestMove !is MoveResign) {
            position = position.moved(bestMove)
        }
        bestMove
    }

    private fun calcMovePoint(move: Move): PieceValue {
        val (row, coloumn) = move.toPosition()
        val nextPosition = position.moved(move)
        val opponentLegalMoves = nextPosition.legalMoves()

        // とりあえず取った駒の価値を足す
        val pieceValue =  when (move) {
            is MoveDrop -> PieceValue(0)
            is MovePosition -> {
                val piece = position.getOrNull(row, coloumn)
                piece?.pieceValue ?: PieceValue(0)
            }
            MoveResign -> error("MoveResign is not expected value.")
        }

        // 置いた駒がすぐ取られてしまう場合、その駒の価値分マイナスする
        val penalty = if (opponentLegalMoves.any { it.toPosition() == move.toPosition() }) {
            nextPosition.getOrNull(row, coloumn)?.pieceValue ?: PieceValue(0)
        } else {
            PieceValue(0)
        }
        // すぐ取られても価値を取り返せそうなら足すとか？
        // 他の自駒が置いたところにきいていれば、置いた個所に聞いている敵駒のうち最低の価値をたす

        // 置いたところからの効き具合（の変化）
        // 置いた後の局面で、legalMoveを取得し、fromが置いたところのMoveに対して、toの位置にある駒の価値(最小)を足す
        // 王の価値はどうしよう？
        val effectedPieceValues = nextPosition.turnChanged().legalMoves().filter {
            when (it) {
                is MoveDrop -> false
                is MovePosition -> {
                    it.fromPosition() == move.toPosition()
                }
                MoveResign -> error("MoveResign is not expected")
            }
        }.mapNotNull {
            val (toRow, toColoumn) = it.toPosition()
            nextPosition.getOrNull(toRow, toColoumn)?.pieceValue
        }

        val effectFromMoveTo = if (effectedPieceValues.size >= 2) {
            // 一つは確実に取れる手になる
            effectedPieceValues.minBy { it }
        } else {
            PieceValue(0)
        }

        // 置いたところに対する駒のききぐあい（の変化）とかみる？参加する駒の価値を足すと面白いかも？
        // 移動する前の位置からの駒の効き具合（の変化）
        // 移動する前の位置への駒聞き具合（の変化）。取られそうになったら逃げるとほぼ同義

        return pieceValue + effectFromMoveTo - penalty * PieceValue(2)
    }
}