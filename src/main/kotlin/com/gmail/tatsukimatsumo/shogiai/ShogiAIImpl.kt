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
    @Volatile
    private var bestTe: Te? = null

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
        bestTe = null
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
        val teList = GenerateMoves.generateLegalMoves(kyokumen) as? List<Any>
        if (teList.isNullOrEmpty()) {
            bestMove = MoveResign
            bestTe = null
        } else {
            var maxPoint = PieceValue(0)

            for (next in teList) {
                val te = next as Te
                var tePoint = PieceValue(0)

                val move = if (te.from == 0) {
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

                    if (kyokumen.get(te.to) != 0) {
                        val opponnentTeban = if (kyokumen.teban == SENTE) GOTE else SENTE
                        val piece = Piece.valueOf((kyokumen.get(te.to) - opponnentTeban) and 8.inv())
                        tePoint = piece.pieceValue(false)
                    }
                    MovePosition("${fromColums.charValue}${fromRow.charValue}${toColums.charValue}${toRow.charValue}$promote")
                }

                if (tePoint >= maxPoint) {
                    bestMove = move
                    bestTe = te
                    maxPoint = tePoint
                }
            }
        }
    }

    override suspend fun onMove() = withContext(defaultDispatcher) {
        kyokumen.move(bestTe)
        kyokumen.teban = if (kyokumen.teban == SENTE) GOTE else SENTE
        bestMove
    }
}
