package com.gmail.tatsukimatsumo.shogiai

import jp.usapyonsoft.lesserpyon.Constants.GOTE
import jp.usapyonsoft.lesserpyon.Constants.SENTE
import jp.usapyonsoft.lesserpyon.GenerateMoves
import jp.usapyonsoft.lesserpyon.Kyokumen
import jp.usapyonsoft.lesserpyon.Te
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.concurrent.Volatile

class ShogiAIImpl(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): ShogiAI {
    @Volatile
    private var bestMove: Move = MoveResign

    private val kyokumen = Kyokumen()

    override suspend fun getProgramName(): ProgramName = withContext(defaultDispatcher) {
        ProgramName("ShogiAI")
    }

    override suspend fun getAuthorName(): AuthorName = withContext(defaultDispatcher) {
        AuthorName("t-matsumo")
    }

    override suspend fun onPrepareForGame() = withContext(defaultDispatcher) {
        kyokumen.initHirate()
        bestMove = MoveResign
    }

    override suspend fun onReceivePosition(position: Position) = withContext(defaultDispatcher) {
        when (position) {
            is PositionHirate -> {
                val lastMove = position.lastMoveOrNull()
                if (lastMove != null) { // movesが無い時は先手番の初手なので何もしない
                    // 相手の手を反映させる
                    val te = when (lastMove) {
                        is MoveDrop -> {
                            val to = lastMove.dropPositonLesserInt()
                            Te(
                                lastMove.dropKoma().lesserIntValue + kyokumen.teban,
                                0,
                                to,
                                false,
                                0
                            )
                        }
                        is MovePosition -> {
                            val from = lastMove.fromPositonLesserInt()
                            val to = lastMove.toPositonLesserInt()
                            Te(
                                kyokumen.get(from),
                                from,
                                to,
                                lastMove.promoted(),
                                kyokumen.get(to)
                            )
                        }
                        MoveResign -> error("This is not expected here.")
                    }
                    kyokumen.move(te)
                    kyokumen.teban = if (kyokumen.teban == SENTE) GOTE else SENTE
                }
            }
            is PositionWithSFEN -> bestMove = MoveResign // 対応していないので投了
        }
    }

    override suspend fun onStartToPonder(timeLimit: TimeLimit) = withContext(defaultDispatcher) {
        // まだ実装しない
    }

    override suspend fun onMove() = withContext(defaultDispatcher) {
        // とりあえずランダム！！
        val te = GenerateMoves.generateLegalMoves(kyokumen).randomOrNull() as Te?
        if (te == null) {
            MoveResign
        } else {
            bestMove = if (te.from == 0) {
                // 駒打ち
                val koma = Piece.valueOf(te.koma - kyokumen.teban)
                val row = Row.valueOf(te.to and 0x0f)
                val colums = Column.valueOf(te.to shr 4)

                MoveDrop("${koma.charValue}*${colums.charValue}${row.charValue}")
            } else {
                // 移動
                val fromRow = Row.valueOf(te.from and 0x0f)
                val fromColums = Column.valueOf(te.from shr 4)

                val toRow = Row.valueOf(te.to and 0x0f)
                val toColums = Column.valueOf(te.to shr 4)

                val promote = if (te.promote) "+" else ""
                MovePosition("${fromColums.charValue}${fromRow.charValue}${toColums.charValue}${toRow.charValue}$promote")
            }
            kyokumen.move(te)
            kyokumen.teban = if (kyokumen.teban == SENTE) GOTE else SENTE
            bestMove
        }
    }
}
