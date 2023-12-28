package com.gmail.tatsukimatsumo.infrastructure

import com.gmail.tatsukimatsumo.shogiengine.InputReader
import java.io.BufferedReader

class InputReaderImpl(private val reader: BufferedReader): InputReader {
    override fun readLine(): String = reader.readLine()
}