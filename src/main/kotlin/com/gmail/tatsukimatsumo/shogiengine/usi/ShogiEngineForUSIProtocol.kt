package com.gmail.tatsukimatsumo.shogiengine.usi

import com.gmail.tatsukimatsumo.shogiai.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.gmail.tatsukimatsumo.shogiengine.InputReader
import com.gmail.tatsukimatsumo.shogiengine.OutPutWriter
import com.gmail.tatsukimatsumo.shogiengine.ShogiEngine
import java.io.Writer
import kotlin.math.min

class ShogiEngineForUSIProtocol(
    private val reader: InputReader,
    private val writer: OutPutWriter,
    private val coroutineScope: CoroutineScope,
    private val shogiAI: ShogiAI
): ShogiEngine {
    override fun execute() {
        // positionコマンドの直後にgoコマンドが来るので、待ち合わせが必要
        // 待ち合わせ用の変数
        var positionJob: Job? = null

        while (true) {
            // 「USIプロトコルとは」の「USIプロトコル全般における注意」に
            // 「エンジンは常に（たとえ思考中であっても）コマンドの受信ができる必要があります。」と記載があるため、
            // 入力以外の処理ははcoroutineで実行する。
            val input = reader.readLine()
            when {
                input == "usi" -> coroutineScope.launch {
                    writer.println("id name ${shogiAI.getProgramName().value}")
                    writer.println("id author ${shogiAI.getAuthorName().value}")
                    writer.println("option name USI_Ponder type check default false")
                    writer.println("usiok")
                }
                input == "isready" -> coroutineScope.launch {
                    shogiAI.onPrepareForGame()
                    writer.println("readyok")
                }
                input.startsWith("setoption") -> { /* 現時点で不要なので実装しない */ }
                input == "usinewgame" -> { /* 現時点で不要なので実装しない */ }
                input.startsWith("position") -> {
                    positionJob = coroutineScope.launch {
                        val positon = when {
                            input.startsWith("position sfen") -> {
                                val sfenStartIndex = input.indexOf("sfen")
                                val movesStartIndex = input.indexOf("moves")
                                val sfen = SFEN(input.substring(sfenStartIndex + 5, movesStartIndex - 1))

                                val moves = input
                                    .substring(min(movesStartIndex + 6, input.lastIndex))
                                    .split(" ")
                                    .map { Move.valueOf(it) }
                                PositionWithSFEN(sfen, moves)
                            }
                            input.startsWith("position startpos") -> {
                                val movesStartIndex = input.indexOf("moves")
                                val moves = if (movesStartIndex != -1) {
                                    input
                                        .substring(min(movesStartIndex + 6, input.lastIndex))
                                        .split(" ")
                                        .map { Move.valueOf(it) }
                                } else {
                                    emptyList()
                                }
                                PositionHirate(moves)
                            }
                            else -> error("The command is not defined on USI protocol (value: $input)")
                        }

                        shogiAI.onReceivePosition(positon)
                    }
                }
                input.startsWith("go") ->  coroutineScope.launch {
                    if (input.startsWith("go mate")) {
                        writer.println("checkmate notimplemented")
                    } else {
                        val timeLimit = when {
                            input.startsWith("go infinite") -> {
                                TimeLimitInFinite
                            }
                            input.contains("byoyomi") -> {
                                val splittedInput = input.split(" ")
                                val timeForFirstPlayer = splittedInput[2].toLong() + splittedInput[6].toLong()
                                val timeForSecondPlayer = splittedInput[4].toLong() + splittedInput[6].toLong()
                                TimeLimitFinite(timeForFirstPlayer, timeForSecondPlayer)
                            }
                            input.contains("binc") -> {
                                val splittedInput = input.split(" ")
                                val timeForFirstPlayer = splittedInput[2].toLong() + splittedInput[6].toLong()
                                val timeForSecondPlayer = splittedInput[4].toLong() + splittedInput[8].toLong()
                                TimeLimitFinite(timeForFirstPlayer, timeForSecondPlayer)
                            }
                            else -> error("The command is not defined on USI protocol (value: $input)")
                        }

                        // positionコマンドの直後にgoコマンドが来るので、待ち合わせる
                        positionJob?.join()
                        shogiAI.onStartToPonder(timeLimit)
                        if (timeLimit is TimeLimitFinite) {
                            val bestMove = shogiAI.onMove()
                            writer.println("bestmove ${bestMove.value}")
                        }
                    }
                }
                input == "stop" -> coroutineScope.launch {
                    val bestMove = shogiAI.onMove()
                    writer.println("bestmove ${bestMove.value}")
                }
                input.startsWith("gameover") -> { /* 現時点で不要なので実装しない */ }
                input == "quit" -> return
                else -> error("The command is not defined on this engine (value: $input).")
            }
        }
    }
}

