package com.gmail.tatsukimatsumo.adapter

import com.gmail.tatsukimatsumo.shogiai.*
import jp.usapyonsoft.lesserpyon.*

// ライブラリへの依存を集約するためのアダプタ
interface PositionGateWay {
    val point: PieceValue
    val turnFor: Turn

    fun moved(move: Move): PositionGateWay
    fun legalMoves(): List<Move>
    fun getOrNull(row: Row, column: Column): Piece?
    fun isGameOver(): Boolean
}

class PositionByLesserpyon private constructor(
    override val point: PieceValue,
    private val kyokumen: Kyokumen
): PositionGateWay {
    override val turnFor: Turn
        get() {
            return if (kyokumen.teban == Constants.SENTE) Turn.FirstPlayer else Turn.SecondPlayer
        }

    constructor(): this(PieceValue(0), Kyokumen()) {
        kyokumen.initHirate()
    }

    override fun moved(move: Move): PositionGateWay {
        val clonedKyokumen = kyokumen.clone() as Kyokumen

        // moveをライブラリの形式へ変換
        val te = when (move) {
            is MoveDrop -> {
                val to = move.dropPositonLesserInt()
                Te(
                    move.dropKoma().lesserIntValue + clonedKyokumen.teban,
                    0,
                    to,
                    false,
                    0
                )
            }
            is MovePosition -> {
                val from = move.fromPositonLesserInt()
                val to = move.toPositonLesserInt()
                Te(
                    clonedKyokumen.get(from),
                    from,
                    to,
                    move.promoted(),
                    clonedKyokumen.get(to)
                )
            }
            MoveResign -> error("This is not expected here.")
        }

        // 先手基準のポイントを計算
        val pointDiff = if (move is MovePosition) {
            val piece = getOrNull(Row.valueOf(te.to and 0x0f), Column.valueOf(te.to shr 4))
            piece?.pieceValue ?: PieceValue(0)
        } else {
            PieceValue(0)
        }
        val nextPoint = if (kyokumen.teban == Constants.SENTE) {
            this.point + pointDiff
        } else {
            this.point - pointDiff
        }

        clonedKyokumen.move(te)
        clonedKyokumen.teban = if (clonedKyokumen.teban == Constants.SENTE) Constants.GOTE else Constants.SENTE

        return PositionByLesserpyon(nextPoint, clonedKyokumen)
    }

    override fun legalMoves(): List<Move> = GenerateMoves
        .generateLegalMoves(kyokumen)
        .map {
            val te = it as Te
            if (te.from == 0) {
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
        }

    override fun getOrNull(row: Row, column: Column): Piece? {
        return when (val koma = Koma.getKomashu(kyokumen.get(row.lesserIntValue + column.lesserIntValue))) {
            Koma.EMPTY -> null
            Koma.FU -> Piece.Pawn
            Koma.KY -> Piece.Lance
            Koma.KE -> Piece.Knight
            Koma.GI -> Piece.Silver
            Koma.KI -> Piece.Gold
            Koma.KA -> Piece.Bishop
            Koma.HI -> Piece.Rook
            Koma.OU -> Piece.King
            Koma.TO -> Piece.PromotedPawn
            Koma.NY -> Piece.PromotedLance
            Koma.NK -> Piece.PromotedKnight
            Koma.NG -> Piece.PromotedSilver
            Koma.UM -> Piece.PromotedBishop
            Koma.RY -> Piece.PromotedRook
            else -> error("the koma(value: $koma) is not defined")
        }
    }

    override fun isGameOver(): Boolean = legalMoves().isEmpty()
}