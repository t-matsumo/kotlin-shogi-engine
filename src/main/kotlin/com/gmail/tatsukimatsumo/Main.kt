package com.gmail.tatsukimatsumo

import com.gmail.tatsukimatsumo.infrastructure.InputReaderImpl
import com.gmail.tatsukimatsumo.infrastructure.OutPutWriterImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import com.gmail.tatsukimatsumo.shogiai.ShogiAIImpl
import com.gmail.tatsukimatsumo.shogiengine.usi.ShogiEngineForUSIProtocol
import java.io.PrintWriter


fun main() {
    PrintWriter(System.out.bufferedWriter()).use { writer ->
        System.`in`.bufferedReader().use { reader ->
            val coroutineScope = CoroutineScope(Dispatchers.Default)
            try {
                ShogiEngineForUSIProtocol(
                    InputReaderImpl(reader),
                    OutPutWriterImpl(writer),
                    coroutineScope,
                    ShogiAIImpl()
                ).execute()
            } finally {
                coroutineScope.cancel()
            }
        }
    }
}
