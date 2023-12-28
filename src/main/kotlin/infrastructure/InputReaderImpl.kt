package infrastructure

import shogiengine.InputReader
import java.io.BufferedReader

class InputReaderImpl(private val reader: BufferedReader): InputReader {
    override fun readLine(): String = reader.readLine()
}